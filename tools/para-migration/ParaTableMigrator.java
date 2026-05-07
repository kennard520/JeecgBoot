import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Copies SQL Server tables whose names start with a configured prefix into a Dameng schema.
 *
 * <p>The tool intentionally fails on existing target tables by default. Use target.tableMode=recreate
 * only after confirming that dropping target PARA_* tables is expected.</p>
 */
public class ParaTableMigrator {
    private static final Set<String> INDEX_TYPES_TO_SKIP = Set.of("HEAP", "CLUSTERED COLUMNSTORE", "NONCLUSTERED COLUMNSTORE");

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java -cp \"<mssql-jdbc.jar>:<dm-jdbc.jar>:.\" ParaTableMigrator para-migrate.properties");
            System.exit(2);
        }

        Config config = Config.load(args[0]);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Class.forName("dm.jdbc.driver.DmDriver");

        try (Connection source = DriverManager.getConnection(config.sourceUrl, config.sourceUsername, config.sourcePassword);
             Connection target = DriverManager.getConnection(config.targetUrl, config.targetUsername, config.targetPassword)) {
            source.setAutoCommit(false);
            target.setAutoCommit(false);

            checkTargetCreateTablePermission(target, config);
            List<SourceTable> tables = loadSourceTables(source, config);
            if (tables.isEmpty()) {
                System.out.println("No source tables found with prefix " + config.tablePrefix);
                return;
            }

            System.out.printf("Found %d source tables with prefix %s%n", tables.size(), config.tablePrefix);
            if ("validate".equals(config.runMode)) {
                validateTables(source, target, config, tables);
                return;
            }
            if ("merge-add".equals(config.runMode)) {
                mergeAddTables(target, config);
                return;
            }

            int ok = 0;
            List<String> failures = new ArrayList<>();
            for (SourceTable table : tables) {
                try {
                    migrateTable(source, target, config, table);
                    target.commit();
                    ok++;
                } catch (Exception e) {
                    target.rollback();
                    failures.add(table.qualifiedName() + " -> " + e.getMessage());
                    System.err.println("[FAILED] " + table.qualifiedName());
                    e.printStackTrace(System.err);
                }
            }

            System.out.printf("Migration finished. success=%d, failed=%d%n", ok, failures.size());
            if (!failures.isEmpty()) {
                System.err.println("Failures:");
                for (String failure : failures) {
                    System.err.println("  - " + failure);
                }
                System.exit(1);
            }
        }
    }

    private static void mergeAddTables(Connection target, Config config) throws SQLException {
        List<String> addTables = loadTargetAddTables(target, config);
        if (addTables.isEmpty()) {
            System.out.println("No target *_ADD tables found with prefix " + config.tablePrefix);
            return;
        }

        int ok = 0;
        List<String> failures = new ArrayList<>();
        for (String addTable : addTables) {
            String baseTable = addTable.substring(0, addTable.length() - 4);
            System.out.println();
            System.out.printf("==> Merge %s into %s%n", addTable, baseTable);
            try {
                mergeOneAddTable(target, config, baseTable, addTable);
                target.commit();
                ok++;
            } catch (Exception e) {
                target.rollback();
                failures.add(addTable + " -> " + e.getMessage());
                System.err.println("[FAILED] " + addTable);
                e.printStackTrace(System.err);
            }
        }
        System.out.printf("ADD merge finished. success=%d, failed=%d%n", ok, failures.size());
        if (!failures.isEmpty()) {
            for (String failure : failures) {
                System.err.println("  - " + failure);
            }
            throw new SQLException("ADD merge failed");
        }
    }

    private static List<String> loadTargetAddTables(Connection target, Config config) throws SQLException {
        String sql = """
                SELECT TABLE_NAME
                FROM ALL_TABLES
                WHERE OWNER = ?
                  AND TABLE_NAME LIKE ? ESCAPE '\\'
                  AND TABLE_NAME LIKE '%\\_ADD' ESCAPE '\\'
                ORDER BY TABLE_NAME
                """;
        List<String> tables = new ArrayList<>();
        try (PreparedStatement ps = target.prepareStatement(sql)) {
            ps.setString(1, config.targetSchema);
            ps.setString(2, escapeSqlLike(config.tablePrefix) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String table = rs.getString("TABLE_NAME");
                    if (config.tableInclude.isEmpty() || config.tableInclude.contains(table.toUpperCase(Locale.ROOT))) {
                        tables.add(table);
                    }
                }
            }
        }
        return tables;
    }

    private static void mergeOneAddTable(Connection target, Config config, String baseTable, String addTable) throws SQLException {
        if (!targetTableExists(target, config.targetSchema, baseTable)) {
            throw new SQLException("base table does not exist: " + baseTable);
        }

        List<String> addColumns = loadTargetColumns(target, config.targetSchema, addTable);
        List<String> baseColumns = loadTargetColumns(target, config.targetSchema, baseTable);
        Set<String> baseSet = new LinkedHashSet<>(baseColumns);
        List<String> commonColumns = addColumns.stream().filter(baseSet::contains).collect(Collectors.toList());
        if (commonColumns.isEmpty()) {
            throw new SQLException("no common columns between " + addTable + " and " + baseTable);
        }

        List<String> pkColumns = loadTargetPrimaryKeyColumns(target, config.targetSchema, baseTable);
        List<String> dedupeColumns = pkColumns.stream().filter(commonColumns::contains).collect(Collectors.toList());
        if (dedupeColumns.isEmpty() && commonColumns.contains("ID")) {
            dedupeColumns = List.of("ID");
        }

        long addCount = countTarget(target, config.targetSchema, addTable);
        long beforeCount = countTarget(target, config.targetSchema, baseTable);
        long duplicateCount = dedupeColumns.isEmpty() ? 0 : countDuplicates(target, config, baseTable, addTable, dedupeColumns);
        if (duplicateCount > 0) {
            System.out.printf("Skip duplicate rows by %s: %d%n", dedupeColumns, duplicateCount);
        }

        String colList = commonColumns.stream().map(ParaTableMigrator::q).collect(Collectors.joining(", "));
        String where = dedupeColumns.isEmpty()
                ? ""
                : " WHERE NOT EXISTS (SELECT 1 FROM " + targetName(config.targetSchema, baseTable) + " b WHERE "
                + dedupeColumns.stream().map(c -> "b." + q(c) + " = a." + q(c)).collect(Collectors.joining(" AND "))
                + ")";
        String insertSql = "INSERT INTO " + targetName(config.targetSchema, baseTable) + " (" + colList + ") SELECT "
                + colList + " FROM " + targetName(config.targetSchema, addTable) + " a" + where;
        executeUpdate(target, insertSql);
        long afterCount = countTarget(target, config.targetSchema, baseTable);
        long inserted = afterCount - beforeCount;
        System.out.printf("Rows: add=%d, baseBefore=%d, inserted=%d, baseAfter=%d%n", addCount, beforeCount, inserted, afterCount);
        if (inserted + duplicateCount != addCount) {
            throw new SQLException("merge row count mismatch for " + addTable + ": add=" + addCount
                    + ", inserted=" + inserted + ", skippedDuplicate=" + duplicateCount);
        }
        execute(target, "DROP TABLE " + targetName(config.targetSchema, addTable));
    }

    private static List<String> loadTargetColumns(Connection target, String schema, String table) throws SQLException {
        String sql = """
                SELECT COLUMN_NAME
                FROM ALL_TAB_COLUMNS
                WHERE OWNER = ? AND TABLE_NAME = ?
                ORDER BY COLUMN_ID
                """;
        List<String> columns = new ArrayList<>();
        try (PreparedStatement ps = target.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        return columns;
    }

    private static List<String> loadTargetPrimaryKeyColumns(Connection target, String schema, String table) throws SQLException {
        String sql = """
                SELECT c.COLUMN_NAME
                FROM ALL_CONSTRAINTS a
                JOIN ALL_CONS_COLUMNS c
                  ON c.OWNER = a.OWNER
                 AND c.CONSTRAINT_NAME = a.CONSTRAINT_NAME
                 AND c.TABLE_NAME = a.TABLE_NAME
                WHERE a.OWNER = ?
                  AND a.TABLE_NAME = ?
                  AND a.CONSTRAINT_TYPE = 'P'
                ORDER BY c.POSITION
                """;
        List<String> columns = new ArrayList<>();
        try (PreparedStatement ps = target.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        return columns;
    }

    private static long countDuplicates(Connection target, Config config, String baseTable, String addTable, List<String> columns) throws SQLException {
        String predicate = columns.stream().map(c -> "b." + q(c) + " = a." + q(c)).collect(Collectors.joining(" AND "));
        String sql = "SELECT COUNT(1) FROM " + targetName(config.targetSchema, addTable)
                + " a WHERE EXISTS (SELECT 1 FROM " + targetName(config.targetSchema, baseTable)
                + " b WHERE " + predicate + ")";
        try (Statement st = target.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static void validateTables(Connection source, Connection target, Config config, List<SourceTable> tables) throws SQLException {
        int ok = 0;
        List<String> failures = new ArrayList<>();
        for (SourceTable table : tables) {
            long sourceCount = countSource(source, table);
            long targetCount;
            try {
                targetCount = countTarget(target, config.targetSchema, table.name);
            } catch (SQLException e) {
                failures.add(table.qualifiedName() + " -> target count failed: " + e.getMessage());
                continue;
            }
            boolean matched = sourceCount == targetCount;
            System.out.printf("Validate %s: source=%d, target=%d, %s%n",
                    table.qualifiedName(), sourceCount, targetCount, matched ? "OK" : "MISMATCH");
            if (matched) {
                ok++;
            } else {
                failures.add(table.qualifiedName() + " -> source=" + sourceCount + ", target=" + targetCount);
            }
        }
        System.out.printf("Validation finished. success=%d, failed=%d%n", ok, failures.size());
        if (!failures.isEmpty()) {
            for (String failure : failures) {
                System.err.println("  - " + failure);
            }
            throw new SQLException("row count validation failed");
        }
    }

    private static void checkTargetCreateTablePermission(Connection target, Config config) throws SQLException {
        String tableName = "__PARA_MIGRATE_PRIV_TEST";
        if (targetTableExists(target, config.targetSchema, tableName)) {
            execute(target, "DROP TABLE " + targetName(config.targetSchema, tableName));
        }
        try {
            execute(target, "CREATE TABLE " + targetName(config.targetSchema, tableName) + " (\"ID\" INT)");
            execute(target, "DROP TABLE " + targetName(config.targetSchema, tableName));
            target.commit();
        } catch (SQLException e) {
            target.rollback();
            throw new SQLException(
                    "Target user cannot create tables in schema " + config.targetSchema
                            + ". Grant CREATE TABLE/RESOURCE privilege or use a DBA-capable target user. Cause: "
                            + e.getMessage(),
                    e);
        }
    }

    private static void migrateTable(Connection source, Connection target, Config config, SourceTable table) throws Exception {
        System.out.println();
        System.out.println("==> " + table.qualifiedName());

        TableDef def = loadTableDefinition(source, table);
        boolean exists = targetTableExists(target, config.targetSchema, table.name);
        prepareTargetTable(target, config, table, def, exists);

        if (config.createPrimaryKeys && !def.primaryKey.columns.isEmpty() && !"truncate".equals(config.tableMode)) {
            // In recreate/fail/append modes, create table already includes the PK when the table is new.
        }

        long sourceCount = countSource(source, table);
        long copied = copyData(source, target, config, table, def);
        createIndexes(target, config, table, def);
        long targetCount = countTarget(target, config.targetSchema, table.name);

        String validation = switch (config.tableMode) {
            case "append" -> targetCount >= copied ? "OK" : "CHECK";
            default -> targetCount == sourceCount ? "OK" : "MISMATCH";
        };
        System.out.printf("Rows: source=%d, copied=%d, target=%d, validation=%s%n", sourceCount, copied, targetCount, validation);
        if (!"OK".equals(validation)) {
            throw new IllegalStateException("row count validation failed");
        }
    }

    private static List<SourceTable> loadSourceTables(Connection source, Config config) throws SQLException {
        String sql = """
                SELECT s.name AS schema_name, t.name AS table_name
                FROM sys.tables t
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                WHERE t.name LIKE ? ESCAPE '\\'
                  AND (? IS NULL OR s.name = ?)
                ORDER BY s.name, t.name
                """;
        String like = escapeSqlLike(config.tablePrefix) + "%";
        try (PreparedStatement ps = source.prepareStatement(sql)) {
            ps.setString(1, like);
            if (config.sourceSchema == null || config.sourceSchema.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(2, config.sourceSchema);
                ps.setString(3, config.sourceSchema);
            }
            List<SourceTable> tables = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    if (config.tableInclude.isEmpty() || config.tableInclude.contains(tableName.toUpperCase(Locale.ROOT))) {
                        tables.add(new SourceTable(rs.getString("schema_name"), tableName));
                    }
                }
            }
            return tables;
        }
    }

    private static TableDef loadTableDefinition(Connection source, SourceTable table) throws SQLException {
        TableDef def = new TableDef();
        def.columns.addAll(loadColumns(source, table));
        def.primaryKey = loadPrimaryKey(source, table);
        def.indexes.addAll(loadIndexes(source, table));
        return def;
    }

    private static List<ColumnDef> loadColumns(Connection source, SourceTable table) throws SQLException {
        String sql = """
                SELECT
                    c.column_id,
                    c.name AS column_name,
                    typ.name AS type_name,
                    c.max_length,
                    c.precision,
                    c.scale,
                    c.is_nullable,
                    c.is_identity,
                    IDENT_SEED(QUOTENAME(s.name) + '.' + QUOTENAME(t.name)) AS identity_seed,
                    IDENT_INCR(QUOTENAME(s.name) + '.' + QUOTENAME(t.name)) AS identity_increment,
                    dc.definition AS default_definition
                FROM sys.columns c
                JOIN sys.tables t ON t.object_id = c.object_id
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                JOIN sys.types typ ON typ.user_type_id = c.user_type_id
                LEFT JOIN sys.default_constraints dc ON dc.object_id = c.default_object_id
                WHERE s.name = ? AND t.name = ?
                ORDER BY c.column_id
                """;
        try (PreparedStatement ps = source.prepareStatement(sql)) {
            ps.setString(1, table.schema);
            ps.setString(2, table.name);
            List<ColumnDef> columns = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ColumnDef c = new ColumnDef();
                    c.name = rs.getString("column_name");
                    c.typeName = rs.getString("type_name");
                    c.maxLength = rs.getInt("max_length");
                    c.precision = rs.getInt("precision");
                    c.scale = rs.getInt("scale");
                    c.nullable = rs.getBoolean("is_nullable");
                    c.identity = rs.getBoolean("is_identity");
                    BigDecimal seed = rs.getBigDecimal("identity_seed");
                    BigDecimal increment = rs.getBigDecimal("identity_increment");
                    c.identitySeed = seed == null ? 1L : seed.longValue();
                    c.identityIncrement = increment == null ? 1L : increment.longValue();
                    c.defaultDefinition = rs.getString("default_definition");
                    columns.add(c);
                }
            }
            return columns;
        }
    }

    private static PrimaryKeyDef loadPrimaryKey(Connection source, SourceTable table) throws SQLException {
        String sql = """
                SELECT kc.name AS pk_name, c.name AS column_name, ic.key_ordinal
                FROM sys.key_constraints kc
                JOIN sys.tables t ON t.object_id = kc.parent_object_id
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                JOIN sys.index_columns ic ON ic.object_id = t.object_id AND ic.index_id = kc.unique_index_id
                JOIN sys.columns c ON c.object_id = t.object_id AND c.column_id = ic.column_id
                WHERE kc.type = 'PK' AND s.name = ? AND t.name = ?
                ORDER BY ic.key_ordinal
                """;
        PrimaryKeyDef pk = new PrimaryKeyDef();
        try (PreparedStatement ps = source.prepareStatement(sql)) {
            ps.setString(1, table.schema);
            ps.setString(2, table.name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pk.name = rs.getString("pk_name");
                    pk.columns.add(rs.getString("column_name"));
                }
            }
        }
        return pk;
    }

    private static List<IndexDef> loadIndexes(Connection source, SourceTable table) throws SQLException {
        String sql = """
                SELECT
                    i.name AS index_name,
                    i.is_unique,
                    i.type_desc,
                    c.name AS column_name,
                    ic.key_ordinal,
                    ic.is_descending_key,
                    ic.is_included_column
                FROM sys.indexes i
                JOIN sys.tables t ON t.object_id = i.object_id
                JOIN sys.schemas s ON s.schema_id = t.schema_id
                JOIN sys.index_columns ic ON ic.object_id = i.object_id AND ic.index_id = i.index_id
                JOIN sys.columns c ON c.object_id = t.object_id AND c.column_id = ic.column_id
                WHERE s.name = ? AND t.name = ?
                  AND i.is_primary_key = 0
                  AND i.is_unique_constraint = 0
                  AND i.name IS NOT NULL
                ORDER BY i.name, ic.key_ordinal
                """;
        Map<String, IndexDef> map = new LinkedHashMap<>();
        try (PreparedStatement ps = source.prepareStatement(sql)) {
            ps.setString(1, table.schema);
            ps.setString(2, table.name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String typeDesc = rs.getString("type_desc");
                    if (INDEX_TYPES_TO_SKIP.contains(typeDesc)) {
                        continue;
                    }
                    boolean included = rs.getBoolean("is_included_column");
                    if (included) {
                        continue;
                    }
                    String name = rs.getString("index_name");
                    IndexDef idx = map.computeIfAbsent(name, n -> {
                        IndexDef i = new IndexDef();
                        i.name = n;
                        try {
                            i.unique = rs.getBoolean("is_unique");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return i;
                    });
                    idx.columns.add(new IndexColumn(rs.getString("column_name"), rs.getBoolean("is_descending_key")));
                }
            }
        }
        return map.values().stream().filter(i -> !i.columns.isEmpty()).collect(Collectors.toList());
    }

    private static void prepareTargetTable(Connection target, Config config, SourceTable table, TableDef def, boolean exists) throws SQLException {
        if (exists) {
            switch (config.tableMode) {
                case "fail" -> throw new IllegalStateException("target table already exists: " + q(config.targetSchema) + "." + q(table.name));
                case "recreate" -> {
                    execute(target, "DROP TABLE " + targetName(config.targetSchema, table.name));
                    createTable(target, config, table, def);
                }
                case "truncate" -> execute(target, "TRUNCATE TABLE " + targetName(config.targetSchema, table.name));
                case "append" -> System.out.println("Target table exists, append mode.");
                default -> throw new IllegalArgumentException("Unsupported target.tableMode=" + config.tableMode);
            }
        } else {
            createTable(target, config, table, def);
        }
    }

    private static void createTable(Connection target, Config config, SourceTable table, TableDef def) throws SQLException {
        List<String> parts = new ArrayList<>();
        for (ColumnDef column : def.columns) {
            parts.add("  " + q(column.name) + " " + mapType(column, config) + (column.nullable ? "" : " NOT NULL"));
        }
        if (config.createPrimaryKeys && !def.primaryKey.columns.isEmpty()) {
            String pkName = q(shortName(def.primaryKey.name == null ? "PK_" + table.name : def.primaryKey.name));
            String pkCols = def.primaryKey.columns.stream().map(ParaTableMigrator::q).collect(Collectors.joining(", "));
            parts.add("  CONSTRAINT " + pkName + " PRIMARY KEY (" + pkCols + ")");
        }
        String ddl = "CREATE TABLE " + targetName(config.targetSchema, table.name) + " (\n" + String.join(",\n", parts) + "\n)";
        execute(target, ddl);
    }

    private static String mapType(ColumnDef c, Config config) {
        String t = c.typeName.toLowerCase(Locale.ROOT);
        return switch (t) {
            case "tinyint" -> "NUMBER(3)";
            case "smallint" -> "SMALLINT";
            case "int" -> config.targetPreserveIdentity && c.identity
                    ? "INT IDENTITY(" + c.identitySeed + ", " + c.identityIncrement + ")"
                    : "INT";
            case "bigint" -> config.targetPreserveIdentity && c.identity
                    ? "BIGINT IDENTITY(" + c.identitySeed + ", " + c.identityIncrement + ")"
                    : "BIGINT";
            case "bit" -> "NUMBER(1)";
            case "decimal", "numeric" -> "DECIMAL(" + Math.max(c.precision, 1) + ", " + c.scale + ")";
            case "money" -> "DECIMAL(19, 4)";
            case "smallmoney" -> "DECIMAL(10, 4)";
            case "float" -> "DOUBLE";
            case "real" -> "FLOAT";
            case "date" -> "DATE";
            case "time" -> "TIME";
            case "datetime", "datetime2", "smalldatetime", "datetimeoffset" -> "TIMESTAMP";
            case "char", "nchar" -> "VARCHAR2(" + stringByteLength(c, config) + ")";
            case "varchar", "nvarchar" -> varcharType(c, config);
            case "text", "ntext", "xml" -> "CLOB";
            case "binary" -> "BINARY(" + byteLength(c) + ")";
            case "varbinary" -> c.maxLength < 0 || c.maxLength > config.maxVarcharBytes ? "BLOB" : "VARBINARY(" + byteLength(c) + ")";
            case "image" -> "BLOB";
            case "uniqueidentifier" -> "VARCHAR2(36)";
            case "timestamp", "rowversion" -> "VARBINARY(8)";
            default -> "CLOB";
        };
    }

    private static String varcharType(ColumnDef c, Config config) {
        int len = stringByteLength(c, config);
        if (c.maxLength < 0 || len > config.maxVarcharBytes) {
            return "CLOB";
        }
        return "VARCHAR2(" + Math.max(len, 1) + ")";
    }

    private static int stringByteLength(ColumnDef c, Config config) {
        long len = (long) charLength(c) * Math.max(config.stringLengthMultiplier, 1);
        return (int) Math.min(Math.max(len, 1), Integer.MAX_VALUE);
    }

    private static int charLength(ColumnDef c) {
        String t = c.typeName.toLowerCase(Locale.ROOT);
        if (t.startsWith("n") && c.maxLength > 0) {
            return Math.max(c.maxLength / 2, 1);
        }
        return c.maxLength <= 0 ? 4000 : c.maxLength;
    }

    private static int byteLength(ColumnDef c) {
        return c.maxLength <= 0 ? 1 : c.maxLength;
    }

    private static long copyData(Connection source, Connection target, Config config, SourceTable table, TableDef def) throws Exception {
        if (def.columns.isEmpty()) {
            return 0;
        }
        boolean hasIdentity = def.columns.stream().anyMatch(c -> c.identity);
        if (hasIdentity && config.targetPreserveIdentity) {
            setIdentityInsert(target, config, table.name, true);
        }
        String colList = def.columns.stream().map(c -> q(c.name)).collect(Collectors.joining(", "));
        String params = def.columns.stream().map(c -> "?").collect(Collectors.joining(", "));
        String selectSql = "SELECT " + colList + " FROM " + sourceName(table.schema, table.name);
        String insertSql = "INSERT INTO " + targetName(config.targetSchema, table.name) + " (" + colList + ") VALUES (" + params + ")";

        long copied = 0;
        try (Statement st = source.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             PreparedStatement insert = target.prepareStatement(insertSql)) {
            st.setFetchSize(config.fetchSize);
            try (ResultSet rs = st.executeQuery(selectSql)) {
                int batch = 0;
                while (rs.next()) {
                    for (int i = 0; i < def.columns.size(); i++) {
                        setParam(insert, i + 1, rs, i + 1, def.columns.get(i));
                    }
                    insert.addBatch();
                    batch++;
                    copied++;
                    if (batch >= config.batchSize) {
                        insert.executeBatch();
                        batch = 0;
                    }
                }
                if (batch > 0) {
                    insert.executeBatch();
                }
            }
        } finally {
            if (hasIdentity && config.targetPreserveIdentity) {
                setIdentityInsert(target, config, table.name, false);
            }
        }
        return copied;
    }

    private static void setParam(PreparedStatement ps, int targetIndex, ResultSet rs, int sourceIndex, ColumnDef c) throws Exception {
        String t = c.typeName.toLowerCase(Locale.ROOT);
        if (Set.of("text", "ntext", "xml").contains(t)) {
            ps.setString(targetIndex, rs.getString(sourceIndex));
            return;
        }
        if ("uniqueidentifier".equals(t)) {
            Object value = rs.getObject(sourceIndex);
            ps.setString(targetIndex, value == null ? null : value.toString());
            return;
        }
        if ("datetimeoffset".equals(t)) {
            Object value = rs.getObject(sourceIndex);
            ps.setTimestamp(targetIndex, toTimestamp(value));
            return;
        }
        Object value = rs.getObject(sourceIndex);
        if (value instanceof Boolean b) {
            ps.setInt(targetIndex, b ? 1 : 0);
        } else if (value instanceof UUID uuid) {
            ps.setString(targetIndex, uuid.toString());
        } else {
            ps.setObject(targetIndex, value);
        }
    }

    private static Timestamp toTimestamp(Object value) throws Exception {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp ts) {
            return ts;
        }
        if (value instanceof Date date) {
            return new Timestamp(date.getTime());
        }
        // mssql-jdbc exposes microsoft.sql.DateTimeOffset.
        try {
            return (Timestamp) value.getClass().getMethod("getTimestamp").invoke(value);
        } catch (ReflectiveOperationException ignored) {
            return Timestamp.valueOf(value.toString().replace('T', ' ').replaceAll(" ?[+-]\\d\\d:\\d\\d$", ""));
        }
    }

    private static void createIndexes(Connection target, Config config, SourceTable table, TableDef def) throws SQLException {
        if (!config.createIndexes || def.indexes.isEmpty()) {
            return;
        }
        for (IndexDef idx : def.indexes) {
            String indexName = q(shortName(idx.name));
            String columns = idx.columns.stream()
                    .map(c -> q(c.name) + (c.desc ? " DESC" : ""))
                    .collect(Collectors.joining(", "));
            String sql = "CREATE " + (idx.unique ? "UNIQUE " : "") + "INDEX " + indexName
                    + " ON " + targetName(config.targetSchema, table.name) + " (" + columns + ")";
            try {
                execute(target, sql);
            } catch (SQLException e) {
                System.err.println("Skip index " + idx.name + ": " + e.getMessage());
            }
        }
    }

    private static boolean targetTableExists(Connection target, String schema, String table) throws SQLException {
        String sql = """
                SELECT COUNT(1)
                FROM ALL_TABLES
                WHERE OWNER = ? AND TABLE_NAME = ?
                """;
        try (PreparedStatement ps = target.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException ignored) {
            DatabaseMetaData md = target.getMetaData();
            try (ResultSet rs = md.getTables(null, schema, table, new String[]{"TABLE"})) {
                return rs.next();
            }
        }
        return false;
    }

    private static long countSource(Connection source, SourceTable table) throws SQLException {
        try (Statement st = source.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(1) FROM " + sourceName(table.schema, table.name))) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static long countTarget(Connection target, String schema, String table) throws SQLException {
        try (Statement st = target.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(1) FROM " + targetName(schema, table))) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static void setIdentityInsert(Connection target, Config config, String table, boolean on) {
        String sql = "SET IDENTITY_INSERT " + targetName(config.targetSchema, table) + (on ? " ON" : " OFF");
        try (Statement st = target.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("failed to execute " + sql + ": " + e.getMessage(), e);
        }
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        System.out.println(sql);
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    private static int executeUpdate(Connection connection, String sql) throws SQLException {
        System.out.println(sql);
        try (Statement st = connection.createStatement()) {
            return st.executeUpdate(sql);
        }
    }

    private static String sourceName(String schema, String table) {
        return "[" + schema.replace("]", "]]") + "].[" + table.replace("]", "]]") + "]";
    }

    private static String targetName(String schema, String table) {
        return q(schema) + "." + q(table);
    }

    private static String q(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private static String shortName(String name) {
        if (name == null || name.isBlank()) {
            return "IDX_" + System.nanoTime();
        }
        return name.length() <= 120 ? name : name.substring(0, 110) + "_" + Integer.toHexString(name.hashCode());
    }

    private static String escapeSqlLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    static class Config {
        String sourceUrl;
        String sourceUsername;
        String sourcePassword;
        String sourceSchema;
        String targetUrl;
        String targetUsername;
        String targetPassword;
        String targetSchema;
        String tablePrefix;
        Set<String> tableInclude;
        String runMode;
        String tableMode;
        boolean targetPreserveIdentity;
        boolean createPrimaryKeys;
        boolean createIndexes;
        int batchSize;
        int fetchSize;
        int stringLengthMultiplier;
        int maxVarcharBytes;

        static Config load(String file) throws Exception {
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                p.load(in);
            }
            Config c = new Config();
            c.sourceUrl = required(p, "source.url");
            c.sourceUsername = required(p, "source.username");
            c.sourcePassword = required(p, "source.password");
            c.sourceSchema = p.getProperty("source.schema", "dbo").trim();
            c.targetUrl = required(p, "target.url");
            c.targetUsername = required(p, "target.username");
            c.targetPassword = required(p, "target.password");
            c.targetSchema = required(p, "target.schema");
            c.tablePrefix = p.getProperty("table.prefix", "PARA_").trim();
            c.tableInclude = Arrays.stream(p.getProperty("table.include", "").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.toUpperCase(Locale.ROOT))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            c.runMode = p.getProperty("run.mode", "migrate").trim().toLowerCase(Locale.ROOT);
            c.tableMode = p.getProperty("target.tableMode", "fail").trim().toLowerCase(Locale.ROOT);
            c.targetPreserveIdentity = Boolean.parseBoolean(p.getProperty("target.preserveIdentity", "true"));
            c.createPrimaryKeys = Boolean.parseBoolean(p.getProperty("target.createPrimaryKeys", "true"));
            c.createIndexes = Boolean.parseBoolean(p.getProperty("target.createIndexes", "true"));
            c.batchSize = Integer.parseInt(p.getProperty("batch.size", "500"));
            c.fetchSize = Integer.parseInt(p.getProperty("fetch.size", "1000"));
            c.stringLengthMultiplier = Integer.parseInt(p.getProperty("target.stringLengthMultiplier", "4"));
            c.maxVarcharBytes = Integer.parseInt(p.getProperty("target.maxVarcharBytes", "4000"));
            if (!Set.of("fail", "recreate", "truncate", "append").contains(c.tableMode)) {
                throw new IllegalArgumentException("target.tableMode must be fail/recreate/truncate/append");
            }
            if (!Set.of("migrate", "validate", "merge-add").contains(c.runMode)) {
                throw new IllegalArgumentException("run.mode must be migrate/validate/merge-add");
            }
            return c;
        }

        private static String required(Properties p, String key) {
            String value = p.getProperty(key);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Missing property: " + key);
            }
            return value.trim();
        }
    }

    static class SourceTable {
        final String schema;
        final String name;

        SourceTable(String schema, String name) {
            this.schema = schema;
            this.name = name;
        }

        String qualifiedName() {
            return schema + "." + name;
        }
    }

    static class TableDef {
        List<ColumnDef> columns = new ArrayList<>();
        PrimaryKeyDef primaryKey = new PrimaryKeyDef();
        List<IndexDef> indexes = new ArrayList<>();
    }

    static class ColumnDef {
        String name;
        String typeName;
        int maxLength;
        int precision;
        int scale;
        boolean nullable;
        boolean identity;
        long identitySeed;
        long identityIncrement;
        String defaultDefinition;
    }

    static class PrimaryKeyDef {
        String name;
        List<String> columns = new ArrayList<>();
    }

    static class IndexDef {
        String name;
        boolean unique;
        List<IndexColumn> columns = new ArrayList<>();
    }

    static class IndexColumn {
        final String name;
        final boolean desc;

        IndexColumn(String name, boolean desc) {
            this.name = name;
            this.desc = desc;
        }
    }
}

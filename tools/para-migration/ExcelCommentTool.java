import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

public class ExcelCommentTool {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage:");
            System.err.println("  java ExcelCommentTool inspect <xls-file> [rows]");
            System.err.println("  java ExcelCommentTool apply <xls-file> <para-migrate.properties>");
            System.exit(2);
        }
        if ("inspect".equals(args[0])) {
            inspect(args);
        } else if ("apply".equals(args[0])) {
            apply(args);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + args[0]);
        }
    }

    private static void inspect(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing xls file");
        }
        String file = args[1];
        int maxRows = args.length >= 3 ? Integer.parseInt(args[2]) : 12;
        DataFormatter formatter = new DataFormatter();
        try (FileInputStream in = new FileInputStream(file); Workbook workbook = WorkbookFactory.create(in)) {
            System.out.println("sheets=" + workbook.getNumberOfSheets());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                System.out.println();
                System.out.println("## " + sheet.getSheetName());
                int lastRow = Math.min(sheet.getLastRowNum(), maxRows - 1);
                for (int r = 0; r <= lastRow; r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(r + 1).append(": ");
                    short lastCell = row.getLastCellNum();
                    for (int c = 0; c < lastCell; c++) {
                        if (c > 0) {
                            sb.append(" | ");
                        }
                        sb.append(formatter.formatCellValue(row.getCell(c)).replace('\n', ' ').trim());
                    }
                    System.out.println(sb);
                }
            }
        }
    }

    private static void apply(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: java ExcelCommentTool apply <xls-file> <para-migrate.properties>");
        }
        String file = args[1];
        DbConfig config = DbConfig.load(args[2]);
        Class.forName("dm.jdbc.driver.DmDriver");

        List<TableComment> comments = readComments(file);
        int tableApplied = 0;
        int columnApplied = 0;
        int tableSkipped = 0;
        int columnSkipped = 0;
        List<String> missingTables = new ArrayList<>();
        List<String> missingColumns = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(config.url, config.username, config.password)) {
            connection.setAutoCommit(false);
            for (TableComment table : comments) {
                if (!targetTableExists(connection, config.schema, table.name)) {
                    tableSkipped++;
                    missingTables.add(table.name);
                    continue;
                }
                if (!isBlank(table.comment)) {
                    execute(connection, "COMMENT ON TABLE " + targetName(config.schema, table.name) + " IS " + sqlString(table.comment));
                    tableApplied++;
                }

                Set<String> targetColumns = loadTargetColumns(connection, config.schema, table.name);
                for (ColumnComment column : table.columns) {
                    if (isBlank(column.name) || isBlank(column.comment)) {
                        continue;
                    }
                    if (!targetColumns.contains(column.name)) {
                        columnSkipped++;
                        missingColumns.add(table.name + "." + column.name);
                        continue;
                    }
                    execute(connection, "COMMENT ON COLUMN " + targetName(config.schema, table.name) + "." + q(column.name)
                            + " IS " + sqlString(column.comment));
                    columnApplied++;
                }
            }
            connection.commit();
        }

        System.out.printf("Excel sheets=%d, table comments applied=%d, skippedTables=%d%n", comments.size(), tableApplied, tableSkipped);
        System.out.printf("Column comments applied=%d, skippedColumns=%d%n", columnApplied, columnSkipped);
        if (!missingTables.isEmpty()) {
            System.out.println("Missing target tables:");
            for (String table : missingTables) {
                System.out.println("  - " + table);
            }
        }
        if (!missingColumns.isEmpty()) {
            System.out.println("Missing target columns:");
            for (String column : missingColumns) {
                System.out.println("  - " + column);
            }
        }
    }

    private static List<TableComment> readComments(String file) throws Exception {
        DataFormatter formatter = new DataFormatter();
        List<TableComment> result = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(file); Workbook workbook = WorkbookFactory.create(in)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String tableName = sheet.getSheetName().trim();
                String tableComment = cell(formatter, sheet.getRow(1), 3);
                int headerRowIndex = findHeaderRow(formatter, sheet);
                if (headerRowIndex < 0) {
                    result.add(new TableComment(tableName, tableComment, List.of()));
                    continue;
                }
                Row header = sheet.getRow(headerRowIndex);
                int nameCol = findCellIndex(formatter, header, "字段名称");
                int commentCol = findCellIndex(formatter, header, "字段说明");
                if (nameCol < 0 || commentCol < 0) {
                    result.add(new TableComment(tableName, tableComment, List.of()));
                    continue;
                }

                List<ColumnComment> columns = new ArrayList<>();
                for (int r = headerRowIndex + 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    String columnName = cell(formatter, row, nameCol);
                    String columnComment = cell(formatter, row, commentCol);
                    if (isBlank(columnName)) {
                        continue;
                    }
                    if ("字段名称".equals(columnName) || columnName.startsWith("EXEC ")) {
                        continue;
                    }
                    columns.add(new ColumnComment(columnName, columnComment));
                }
                result.add(new TableComment(tableName, tableComment, columns));
            }
        }
        return result;
    }

    private static int findHeaderRow(DataFormatter formatter, Sheet sheet) {
        for (int r = 0; r <= Math.min(sheet.getLastRowNum(), 20); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            if (findCellIndex(formatter, row, "字段名称") >= 0 && findCellIndex(formatter, row, "字段说明") >= 0) {
                return r;
            }
        }
        return -1;
    }

    private static int findCellIndex(DataFormatter formatter, Row row, String text) {
        if (row == null) {
            return -1;
        }
        for (int c = 0; c < row.getLastCellNum(); c++) {
            if (text.equals(cell(formatter, row, c))) {
                return c;
            }
        }
        return -1;
    }

    private static String cell(DataFormatter formatter, Row row, int index) {
        if (row == null || index < 0) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(index)).replace('\n', ' ').trim();
    }

    private static boolean targetTableExists(Connection connection, String schema, String table) throws SQLException {
        String sql = "SELECT COUNT(1) FROM ALL_TABLES WHERE OWNER = ? AND TABLE_NAME = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, schema);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private static Set<String> loadTargetColumns(Connection connection, String schema, String table) throws SQLException {
        String sql = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE OWNER = ? AND TABLE_NAME = ?";
        Set<String> columns = new LinkedHashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    private static void execute(Connection connection, String sql) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    private static String targetName(String schema, String table) {
        return q(schema) + "." + q(table);
    }

    private static String q(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private static String sqlString(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    record TableComment(String name, String comment, List<ColumnComment> columns) {
    }

    record ColumnComment(String name, String comment) {
    }

    static class DbConfig {
        String url;
        String username;
        String password;
        String schema;

        static DbConfig load(String file) throws Exception {
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                p.load(in);
            }
            DbConfig c = new DbConfig();
            c.url = required(p, "target.url");
            c.username = required(p, "target.username");
            c.password = required(p, "target.password");
            c.schema = required(p, "target.schema");
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
}

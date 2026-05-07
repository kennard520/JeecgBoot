import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Generates the jeecg-module-custom PARA_* backend classes from the current Dameng schema metadata.
 */
public class ParaModuleGenerator {
    private static final String BASE_PACKAGE = "org.jeecg.modules.custom.para";
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
            "volatile", "while"));

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java ParaModuleGenerator <para-migrate.properties> <jeecg-module-custom-dir>");
            System.exit(2);
        }
        DbConfig config = DbConfig.load(args[0]);
        Path moduleDir = Path.of(args[1]).toAbsolutePath().normalize();
        Class.forName("dm.jdbc.driver.DmDriver");
        try (Connection connection = DriverManager.getConnection(config.url, config.username, config.password)) {
            List<TableMeta> tables = loadTables(connection, config.schema);
            Path javaRoot = moduleDir.resolve("src/main/java");
            generatePackageInfo(javaRoot);
            for (TableMeta table : tables) {
                generateTable(javaRoot, table);
            }
            System.out.printf("Generated PARA module logic. tables=%d, files=%d, output=%s%n",
                    tables.size(), tables.size() * 6 + 1, javaRoot.resolve(BASE_PACKAGE.replace('.', '/')));
            List<TableMeta> withoutPk = tables.stream().filter(t -> t.idColumn == null).toList();
            if (!withoutPk.isEmpty()) {
                System.out.println("Tables without primary key metadata, generated with first column as @TableId:");
                for (TableMeta table : withoutPk) {
                    System.out.println("  - " + table.tableName + "." + table.columns.get(0).columnName);
                }
            }
        }
    }

    private static List<TableMeta> loadTables(Connection connection, String schema) throws SQLException {
        String tableSql = """
                SELECT t.TABLE_NAME, tc.COMMENTS
                FROM ALL_TABLES t
                LEFT JOIN ALL_TAB_COMMENTS tc
                  ON tc.OWNER = t.OWNER
                 AND tc.TABLE_NAME = t.TABLE_NAME
                WHERE t.OWNER = ?
                  AND t.TABLE_NAME LIKE 'PARA\\_%' ESCAPE '\\'
                ORDER BY t.TABLE_NAME
                """;
        List<TableMeta> tables = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(tableSql)) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TableMeta table = new TableMeta();
                    table.tableName = rs.getString("TABLE_NAME");
                    table.className = toClassName(table.tableName);
                    table.varName = lowerFirst(table.className);
                    table.pathName = table.varName;
                    table.comment = defaultText(rs.getString("COMMENTS"), table.tableName);
                    tables.add(table);
                }
            }
        }
        for (TableMeta table : tables) {
            table.columns = loadColumns(connection, schema, table.tableName);
            Set<String> pkColumns = loadPrimaryKeys(connection, schema, table.tableName);
            ColumnMeta fallback = null;
            for (ColumnMeta column : table.columns) {
                column.pk = pkColumns.contains(column.columnName);
                if (column.pk && table.idColumn == null) {
                    table.idColumn = column;
                }
                if ("ID".equals(column.columnName)) {
                    fallback = column;
                }
            }
            if (table.idColumn == null) {
                table.idColumn = fallback != null ? fallback : table.columns.get(0);
                table.idColumn.syntheticId = true;
            }
            table.columns.sort(Comparator.comparingInt(c -> c.position));
        }
        return tables;
    }

    private static List<ColumnMeta> loadColumns(Connection connection, String schema, String tableName) throws SQLException {
        String columnSql = """
                SELECT c.COLUMN_NAME,
                       c.DATA_TYPE,
                       c.DATA_LENGTH,
                       c.DATA_PRECISION,
                       c.DATA_SCALE,
                       c.NULLABLE,
                       c.COLUMN_ID,
                       cc.COMMENTS
                FROM ALL_TAB_COLUMNS c
                LEFT JOIN ALL_COL_COMMENTS cc
                  ON cc.OWNER = c.OWNER
                 AND cc.TABLE_NAME = c.TABLE_NAME
                 AND cc.COLUMN_NAME = c.COLUMN_NAME
                WHERE c.OWNER = ?
                  AND c.TABLE_NAME = ?
                ORDER BY c.COLUMN_ID
                """;
        List<ColumnMeta> columns = new ArrayList<>();
        Map<String, Integer> javaNameCount = new LinkedHashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(columnSql)) {
            ps.setString(1, schema);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ColumnMeta column = new ColumnMeta();
                    column.columnName = rs.getString("COLUMN_NAME");
                    column.dataType = defaultText(rs.getString("DATA_TYPE"), "");
                    column.dataLength = intValue(rs, "DATA_LENGTH");
                    column.precision = intValue(rs, "DATA_PRECISION");
                    column.scale = intValue(rs, "DATA_SCALE");
                    column.nullable = "Y".equalsIgnoreCase(rs.getString("NULLABLE"));
                    column.position = intValue(rs, "COLUMN_ID");
                    column.comment = defaultText(rs.getString("COMMENTS"), column.columnName);
                    column.javaName = uniqueJavaName(toFieldName(column.columnName), javaNameCount);
                    columns.add(column);
                }
            }
        }
        return columns;
    }

    private static Set<String> loadPrimaryKeys(Connection connection, String schema, String tableName) throws SQLException {
        String pkSql = """
                SELECT cc.COLUMN_NAME
                FROM ALL_CONSTRAINTS c
                JOIN ALL_CONS_COLUMNS cc
                  ON cc.OWNER = c.OWNER
                 AND cc.CONSTRAINT_NAME = c.CONSTRAINT_NAME
                 AND cc.TABLE_NAME = c.TABLE_NAME
                WHERE c.OWNER = ?
                  AND c.TABLE_NAME = ?
                  AND c.CONSTRAINT_TYPE = 'P'
                ORDER BY cc.POSITION
                """;
        Set<String> columns = new LinkedHashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(pkSql)) {
            ps.setString(1, schema);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        }
        return columns;
    }

    private static void generatePackageInfo(Path javaRoot) throws IOException {
        Path file = packageDir(javaRoot).resolve("package-info.java");
        String content = """
                /**
                 * Customs reference parameter tables generated from PARA_* Dameng metadata.
                 */
                package org.jeecg.modules.custom.para;
                """;
        write(file, content);
    }

    private static void generateTable(Path javaRoot, TableMeta table) throws IOException {
        generateEntity(javaRoot, table);
        generateMapper(javaRoot, table);
        generateMapperXml(javaRoot, table);
        generateService(javaRoot, table);
        generateServiceImpl(javaRoot, table);
        generateController(javaRoot, table);
    }

    private static void generateEntity(Path javaRoot, TableMeta table) throws IOException {
        boolean hasDate = table.columns.stream().anyMatch(c -> "Date".equals(javaType(c, table)));
        boolean hasBigDecimal = table.columns.stream().anyMatch(c -> "BigDecimal".equals(javaType(c, table)));
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(BASE_PACKAGE).append(".entity;\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.IdType;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableField;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableId;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableName;\n");
        if (hasDate) {
            sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
        }
        sb.append("import io.swagger.v3.oas.annotations.media.Schema;\n");
        sb.append("import lombok.Data;\n");
        sb.append("import lombok.EqualsAndHashCode;\n");
        sb.append("import lombok.experimental.Accessors;\n");
        sb.append("import org.jeecgframework.poi.excel.annotation.Excel;\n");
        if (hasDate) {
            sb.append("import org.springframework.format.annotation.DateTimeFormat;\n");
        }
        sb.append("import java.io.Serializable;\n");
        if (hasBigDecimal) {
            sb.append("import java.math.BigDecimal;\n");
        }
        if (hasDate) {
            sb.append("import java.util.Date;\n");
        }
        sb.append("\n");
        appendClassJavadoc(sb, table.comment + " " + table.className + "。", "根据达梦库当前 PARA_* 表结构生成，字段注释来自数据库 COMMENT 信息。");
        sb.append("@Data\n");
        sb.append("@EqualsAndHashCode(callSuper = false)\n");
        sb.append("@Accessors(chain = true)\n");
        sb.append("@Schema(description = \"").append(javaString(table.comment)).append("\")\n");
        sb.append("@TableName(\"").append(javaString(table.tableName)).append("\")\n");
        sb.append("public class ").append(table.className).append(" implements Serializable {\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");
        for (ColumnMeta column : table.columns) {
            String type = javaType(column, table);
            appendFieldJavadoc(sb, column);
            boolean id = column == table.idColumn;
            if (id) {
                sb.append("    @TableId(value = \"").append(javaString(column.columnName)).append("\", type = ")
                        .append(idType(column)).append(")\n");
            } else {
                sb.append("    @TableField(\"").append(javaString(column.columnName)).append("\")\n");
            }
            if ("Date".equals(type)) {
                sb.append("    @Excel(name = \"").append(javaString(column.comment)).append("\", width = 20, format = \"yyyy-MM-dd HH:mm:ss\")\n");
                sb.append("    @JsonFormat(timezone = \"GMT+8\", pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
                sb.append("    @DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
            } else {
                sb.append("    @Excel(name = \"").append(javaString(column.comment)).append("\", width = 15)\n");
            }
            sb.append("    @Schema(description = \"").append(javaString(column.comment)).append("\")\n");
            sb.append("    private ").append(type).append(" ").append(column.javaName).append(";\n\n");
        }
        sb.append("}\n");
        write(packageDir(javaRoot).resolve("entity").resolve(table.className + ".java"), sb.toString());
    }

    private static void generateMapper(Path javaRoot, TableMeta table) throws IOException {
        String content = "package " + BASE_PACKAGE + ".mapper;\n\n"
                + "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n"
                + "import " + BASE_PACKAGE + ".entity." + table.className + ";\n\n"
                + "/**\n"
                + " * " + javadoc(table.comment + " " + table.className + " Mapper。") + "\n"
                + " *\n"
                + " * <p>继承 MyBatis-Plus BaseMapper，提供 " + javadoc(table.comment) + " 的基础增删改查能力。</p>\n"
                + " *\n"
                + " * @author jeecg-boot\n"
                + " * @since 3.9.1\n"
                + " */\n"
                + "public interface " + table.className + "Mapper extends BaseMapper<" + table.className + "> {\n"
                + "}\n";
        write(packageDir(javaRoot).resolve("mapper").resolve(table.className + "Mapper.java"), content);
    }

    private static void generateMapperXml(Path javaRoot, TableMeta table) throws IOException {
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n"
                + "<mapper namespace=\"" + BASE_PACKAGE + ".mapper." + table.className + "Mapper\">\n"
                + "    <!--\n"
                + "      " + xmlComment(table.comment + " " + table.className) + "\n"
                + "      当前模块先使用 MyBatis-Plus BaseMapper 提供的通用 SQL；如需复杂关联查询，后续在此补充自定义 SQL。\n"
                + "    -->\n"
                + "</mapper>\n";
        write(packageDir(javaRoot).resolve("mapper/xml").resolve(table.className + "Mapper.xml"), content);
    }

    private static void generateService(Path javaRoot, TableMeta table) throws IOException {
        String content = "package " + BASE_PACKAGE + ".service;\n\n"
                + "import com.baomidou.mybatisplus.extension.service.IService;\n"
                + "import " + BASE_PACKAGE + ".entity." + table.className + ";\n\n"
                + "/**\n"
                + " * " + javadoc(table.comment + " " + table.className + " Service。") + "\n"
                + " *\n"
                + " * <p>封装 " + javadoc(table.comment) + " 的业务操作入口，目前提供 MyBatis-Plus 标准服务能力。</p>\n"
                + " *\n"
                + " * @author jeecg-boot\n"
                + " * @since 3.9.1\n"
                + " */\n"
                + "public interface I" + table.className + "Service extends IService<" + table.className + "> {\n"
                + "}\n";
        write(packageDir(javaRoot).resolve("service").resolve("I" + table.className + "Service.java"), content);
    }

    private static void generateServiceImpl(Path javaRoot, TableMeta table) throws IOException {
        String content = "package " + BASE_PACKAGE + ".service.impl;\n\n"
                + "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n"
                + "import " + BASE_PACKAGE + ".entity." + table.className + ";\n"
                + "import " + BASE_PACKAGE + ".mapper." + table.className + "Mapper;\n"
                + "import " + BASE_PACKAGE + ".service.I" + table.className + "Service;\n"
                + "import org.springframework.stereotype.Service;\n\n"
                + "/**\n"
                + " * " + javadoc(table.comment + " " + table.className + " Service 实现。") + "\n"
                + " *\n"
                + " * <p>当前实现委托 MyBatis-Plus ServiceImpl 完成基础 CRUD；复杂业务规则应在本类中集中扩展。</p>\n"
                + " *\n"
                + " * @author jeecg-boot\n"
                + " * @since 3.9.1\n"
                + " */\n"
                + "@Service\n"
                + "public class " + table.className + "ServiceImpl extends ServiceImpl<" + table.className + "Mapper, "
                + table.className + "> implements I" + table.className + "Service {\n"
                + "}\n";
        write(packageDir(javaRoot).resolve("service/impl").resolve(table.className + "ServiceImpl.java"), content);
    }

    private static void generateController(Path javaRoot, TableMeta table) throws IOException {
        String idName = table.idColumn.columnName;
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(BASE_PACKAGE).append(".controller;\n\n");
        sb.append("import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;\n");
        sb.append("import com.baomidou.mybatisplus.core.metadata.IPage;\n");
        sb.append("import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n");
        sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
        sb.append("import io.swagger.v3.oas.annotations.Parameter;\n");
        sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        sb.append("import jakarta.servlet.http.HttpServletRequest;\n");
        sb.append("import jakarta.servlet.http.HttpServletResponse;\n");
        sb.append("import lombok.extern.slf4j.Slf4j;\n");
        sb.append("import org.jeecg.common.api.vo.Result;\n");
        sb.append("import org.jeecg.common.aspect.annotation.AutoLog;\n");
        sb.append("import org.jeecg.common.aspect.annotation.PermissionData;\n");
        sb.append("import org.jeecg.common.constant.CommonConstant;\n");
        sb.append("import org.jeecg.common.system.base.controller.JeecgController;\n");
        sb.append("import org.jeecg.common.system.query.QueryGenerator;\n");
        sb.append("import ").append(BASE_PACKAGE).append(".entity.").append(table.className).append(";\n");
        sb.append("import ").append(BASE_PACKAGE).append(".service.I").append(table.className).append("Service;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.web.bind.annotation.DeleteMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.GetMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.PostMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.PutMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestBody;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMethod;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestParam;\n");
        sb.append("import org.springframework.web.bind.annotation.RestController;\n");
        sb.append("import org.springframework.web.servlet.ModelAndView;\n\n");
        sb.append("import java.util.Arrays;\n\n");
        appendClassJavadoc(sb, table.comment + " " + table.className + " 控制器。",
                "提供分页查询、新增、编辑、删除、导入和导出等基础接口；字段含义见实体类注释。");
        sb.append("@Slf4j\n");
        sb.append("@Tag(name = \"").append(javaString(table.comment)).append(" ").append(table.className).append("\")\n");
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/custom/para/").append(table.pathName).append("\")\n");
        sb.append("public class ").append(table.className).append("Controller extends JeecgController<")
                .append(table.className).append(", I").append(table.className).append("Service> {\n");
        sb.append("    @Autowired\n");
        sb.append("    private I").append(table.className).append("Service ").append(table.varName).append("Service;\n\n");
        appendMethodJavadoc(sb, "分页查询 " + table.comment + "。", "查询条件由 QueryGenerator 根据请求参数和实体字段自动组装，默认按 "
                + idName + " 倒序返回。");
        sb.append("    @Operation(summary = \"分页查询").append(javaString(table.comment)).append("\")\n");
        sb.append("    @GetMapping(value = \"/list\")\n");
        sb.append("    @PermissionData(pageComponent = \"custom/para/").append(table.pathName).append("\")\n");
        sb.append("    public Result<IPage<").append(table.className).append(">> queryPageList(")
                .append(table.className).append(" ").append(table.varName).append(",\n");
        sb.append("                                                      @RequestParam(name = \"pageNo\", defaultValue = \"1\") Integer pageNo,\n");
        sb.append("                                                      @RequestParam(name = \"pageSize\", defaultValue = \"10\") Integer pageSize,\n");
        sb.append("                                                      HttpServletRequest req) {\n");
        sb.append("        QueryWrapper<").append(table.className).append("> queryWrapper = QueryGenerator.initQueryWrapper(")
                .append(table.varName).append(", req.getParameterMap());\n");
        sb.append("        queryWrapper.orderByDesc(\"").append(javaString(idName)).append("\");\n");
        sb.append("        Page<").append(table.className).append("> page = new Page<>(pageNo, pageSize);\n");
        sb.append("        IPage<").append(table.className).append("> pageList = ").append(table.varName)
                .append("Service.page(page, queryWrapper);\n");
        sb.append("        log.debug(\"分页查询").append(javaString(table.comment)).append("，当前页={}，每页数量={}，总数={}\", pageNo, pageSize, pageList.getTotal());\n");
        sb.append("        return Result.OK(pageList);\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "新增 " + table.comment + "。", null);
        sb.append("    @AutoLog(value = \"新增").append(javaString(table.comment)).append("\")\n");
        sb.append("    @Operation(summary = \"新增").append(javaString(table.comment)).append("\")\n");
        sb.append("    @PostMapping(value = \"/add\")\n");
        sb.append("    public Result<?> add(@RequestBody ").append(table.className).append(" ").append(table.varName).append(") {\n");
        sb.append("        ").append(table.varName).append("Service.save(").append(table.varName).append(");\n");
        sb.append("        return Result.OK(\"添加成功！\");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "编辑 " + table.comment + "。", null);
        sb.append("    @AutoLog(value = \"编辑").append(javaString(table.comment)).append("\", operateType = CommonConstant.OPERATE_TYPE_3)\n");
        sb.append("    @Operation(summary = \"编辑").append(javaString(table.comment)).append("\")\n");
        sb.append("    @PutMapping(value = \"/edit\")\n");
        sb.append("    public Result<?> edit(@RequestBody ").append(table.className).append(" ").append(table.varName).append(") {\n");
        sb.append("        ").append(table.varName).append("Service.updateById(").append(table.varName).append(");\n");
        sb.append("        return Result.OK(\"更新成功！\");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "通过 ID 删除 " + table.comment + "。", null);
        sb.append("    @AutoLog(value = \"删除").append(javaString(table.comment)).append("\")\n");
        sb.append("    @Operation(summary = \"通过ID删除").append(javaString(table.comment)).append("\")\n");
        sb.append("    @DeleteMapping(value = \"/delete\")\n");
        sb.append("    public Result<?> delete(@RequestParam(name = \"id\", required = true) String id) {\n");
        sb.append("        ").append(table.varName).append("Service.removeById(id);\n");
        sb.append("        return Result.OK(\"删除成功！\");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "批量删除 " + table.comment + "。", null);
        sb.append("    @AutoLog(value = \"批量删除").append(javaString(table.comment)).append("\")\n");
        sb.append("    @Operation(summary = \"批量删除").append(javaString(table.comment)).append("\")\n");
        sb.append("    @DeleteMapping(value = \"/deleteBatch\")\n");
        sb.append("    public Result<?> deleteBatch(@RequestParam(name = \"ids\", required = true) String ids) {\n");
        sb.append("        ").append(table.varName).append("Service.removeByIds(Arrays.asList(ids.split(\",\")));\n");
        sb.append("        return Result.OK(\"批量删除成功！\");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "通过 ID 查询 " + table.comment + " 明细。", null);
        sb.append("    @Operation(summary = \"通过ID查询").append(javaString(table.comment)).append("\")\n");
        sb.append("    @GetMapping(value = \"/queryById\")\n");
        sb.append("    public Result<").append(table.className).append("> queryById(@Parameter(name = \"id\", description = \"主键ID\", required = true)\n");
        sb.append("                                          @RequestParam(name = \"id\", required = true) String id) {\n");
        sb.append("        ").append(table.className).append(" ").append(table.varName).append(" = ")
                .append(table.varName).append("Service.getById(id);\n");
        sb.append("        return Result.OK(").append(table.varName).append(");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "导出 " + table.comment + " Excel。", null);
        sb.append("    @Operation(summary = \"导出").append(javaString(table.comment)).append("Excel\")\n");
        sb.append("    @RequestMapping(value = \"/exportXls\")\n");
        sb.append("    public ModelAndView exportXls(HttpServletRequest request, ").append(table.className).append(" ")
                .append(table.varName).append(") {\n");
        sb.append("        return super.exportXls(request, ").append(table.varName).append(", ").append(table.className)
                .append(".class, \"").append(javaString(table.comment)).append("\");\n");
        sb.append("    }\n\n");
        appendMethodJavadoc(sb, "从 Excel 导入 " + table.comment + "。", null);
        sb.append("    @Operation(summary = \"从Excel导入").append(javaString(table.comment)).append("\")\n");
        sb.append("    @RequestMapping(value = \"/importExcel\", method = RequestMethod.POST)\n");
        sb.append("    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {\n");
        sb.append("        return super.importExcel(request, response, ").append(table.className).append(".class);\n");
        sb.append("    }\n");
        sb.append("}\n");
        write(packageDir(javaRoot).resolve("controller").resolve(table.className + "Controller.java"), sb.toString());
    }

    private static String javaType(ColumnMeta column, TableMeta table) {
        String type = column.dataType.toUpperCase(Locale.ROOT);
        if (type.contains("CHAR") || type.contains("TEXT") || type.contains("CLOB") || type.contains("XML")) {
            return "String";
        }
        if (type.contains("DATE") || type.contains("TIME")) {
            return "Date";
        }
        if (type.contains("BLOB") || type.contains("BINARY") || type.contains("RAW") || type.contains("IMAGE")) {
            return "byte[]";
        }
        if (type.contains("NUMBER") || type.contains("DECIMAL") || type.contains("NUMERIC")) {
            if (column == table.idColumn) {
                return "Long";
            }
            if (column.scale > 0) {
                return "BigDecimal";
            }
            if (column.precision > 0 && column.precision <= 9) {
                return "Integer";
            }
            if (column.precision > 0 && column.precision <= 18) {
                return "Long";
            }
            return "BigDecimal";
        }
        if (type.equals("INT") || type.equals("INTEGER") || type.equals("SMALLINT") || type.equals("TINYINT")) {
            return column == table.idColumn ? "Long" : "Integer";
        }
        if (type.equals("BIGINT")) {
            return "Long";
        }
        if (type.contains("DOUBLE") || type.contains("FLOAT") || type.contains("REAL")) {
            return "BigDecimal";
        }
        return "String";
    }

    private static String idType(ColumnMeta column) {
        String type = column.dataType.toUpperCase(Locale.ROOT);
        if ("ID".equals(column.columnName) && !column.syntheticId
                && (type.contains("INT") || type.contains("NUMBER") || type.contains("DECIMAL") || type.contains("NUMERIC"))) {
            return "IdType.AUTO";
        }
        return "IdType.INPUT";
    }

    private static Path packageDir(Path javaRoot) {
        return javaRoot.resolve(BASE_PACKAGE.replace('.', '/'));
    }

    private static void appendClassJavadoc(StringBuilder sb, String title, String detail) {
        sb.append("/**\n");
        sb.append(" * ").append(javadoc(title)).append("\n");
        if (detail != null && !detail.isBlank()) {
            sb.append(" *\n");
            sb.append(" * <p>").append(javadoc(detail)).append("</p>\n");
        }
        sb.append(" *\n");
        sb.append(" * @author jeecg-boot\n");
        sb.append(" * @since 3.9.1\n");
        sb.append(" */\n");
    }

    private static void appendMethodJavadoc(StringBuilder sb, String title, String detail) {
        sb.append("    /**\n");
        sb.append("     * ").append(javadoc(title)).append("\n");
        if (detail != null && !detail.isBlank()) {
            sb.append("     *\n");
            sb.append("     * <p>").append(javadoc(detail)).append("</p>\n");
        }
        sb.append("     */\n");
    }

    private static void appendFieldJavadoc(StringBuilder sb, ColumnMeta column) {
        sb.append("    /**\n");
        sb.append("     * ").append(javadoc(column.comment)).append("；字段名: ").append(javadoc(column.columnName))
                .append("；数据类型: ").append(javadoc(column.dataType));
        StringJoiner joiner = new StringJoiner("；");
        if (column.dataLength > 0) {
            joiner.add("长度: " + column.dataLength);
        }
        if (column.precision > 0) {
            joiner.add("精度: " + column.precision);
        }
        if (column.scale > 0) {
            joiner.add("小数位: " + column.scale);
        }
        joiner.add("可空: " + (column.nullable ? "是" : "否"));
        sb.append("；").append(joiner).append("\n");
        sb.append("     */\n");
    }

    private static String toClassName(String tableName) {
        return Arrays.stream(tableName.toLowerCase(Locale.ROOT).split("_+"))
                .filter(s -> !s.isBlank())
                .map(ParaModuleGenerator::upperFirst)
                .collect(Collectors.joining());
    }

    private static String toFieldName(String columnName) {
        String[] parts = columnName.toLowerCase(Locale.ROOT).split("_+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (sb.length() == 0) {
                sb.append(part);
            } else {
                sb.append(upperFirst(part));
            }
        }
        String result = sb.length() == 0 ? "field" : sb.toString();
        if (!Character.isJavaIdentifierStart(result.charAt(0))) {
            result = "field" + upperFirst(result);
        }
        result = result.chars()
                .mapToObj(ch -> Character.isJavaIdentifierPart(ch) ? String.valueOf((char) ch) : "_")
                .collect(Collectors.joining());
        if (JAVA_KEYWORDS.contains(result)) {
            result = result + "Field";
        }
        return result;
    }

    private static String uniqueJavaName(String name, Map<String, Integer> counts) {
        int count = counts.getOrDefault(name, 0);
        counts.put(name, count + 1);
        return count == 0 ? name : name + (count + 1);
    }

    private static String upperFirst(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private static String lowerFirst(String value) {
        return value.substring(0, 1).toLowerCase(Locale.ROOT) + value.substring(1);
    }

    private static int intValue(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return value == null ? 0 : value.intValue();
    }

    private static String defaultText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.replace('\n', ' ').replace('\r', ' ').trim();
    }

    private static String javaString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String javadoc(String value) {
        return defaultText(value, "").replace("*/", "* /");
    }

    private static String xmlComment(String value) {
        return defaultText(value, "").replace("--", "- -");
    }

    private static void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    static class TableMeta {
        String tableName;
        String className;
        String varName;
        String pathName;
        String comment;
        List<ColumnMeta> columns = new ArrayList<>();
        ColumnMeta idColumn;
    }

    static class ColumnMeta {
        String columnName;
        String javaName;
        String dataType;
        int dataLength;
        int precision;
        int scale;
        boolean nullable;
        int position;
        String comment;
        boolean pk;
        boolean syntheticId;
    }

    static class DbConfig {
        String url;
        String username;
        String password;
        String schema;

        static DbConfig load(String file) throws IOException {
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

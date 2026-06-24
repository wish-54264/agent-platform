package com.agent.mcp.servers;

import com.agent.mcp.core.McpServer;
import com.agent.mcp.core.SchemaFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;

/**
 * 数据库 MCP Server — 暴露 SQL 查询和表管理工具。
 * <p>
 * 使用 H2 内嵌数据库作为演示（零配置，进程启动即用）。
 * 实际生产环境可改为 PostgreSQL / MySQL。
 *
 * <h3>暴露的工具：</h3>
 * <ul>
 *   <li>{@code query_database} — 执行只读 SQL 查询</li>
 *   <li>{@code list_tables} — 列出所有表</li>
 *   <li>{@code describe_table} — 查看表结构</li>
 * </ul>
 *
 * <h3>运行方式：</h3>
 * <pre>{@code
 * java -cp agent-mcp.jar com.agent.mcp.servers.DatabaseMcpServer
 * }</pre>
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #initDataSource()} — 初始化数据源
 * {@link #main(String[])} — 启动入口
 */
public class DatabaseMcpServer extends McpServer {

    private DataSource dataSource;
    private final ObjectMapper mapper = new ObjectMapper();

    public DatabaseMcpServer() {
        initDataSource();
        registerTools();
    }

    // ==========================================================
    // 数据源初始化（你来写）
    // ==========================================================

    /**
     * TODO: 初始化 H2 内嵌数据库。
     *
     * <h3>实现提示：</h3>
     * <pre>{@code
     * HikariConfig config = new HikariConfig();
     * config.setJdbcUrl("jdbc:h2:mem:agentdb;DB_CLOSE_DELAY=-1");
     * config.setUsername("sa");
     * config.setPassword("");
     * this.dataSource = new HikariDataSource(config);
     *
     * // 创建示例表
     * try (Connection conn = dataSource.getConnection();
     *      Statement stmt = conn.createStatement()) {
     *     stmt.execute("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(200))");
     *     stmt.execute("INSERT INTO users VALUES (1, '张三', 'zhangsan@example.com')");
     *     stmt.execute("INSERT INTO users VALUES (2, '李四', 'lisi@example.com')");
     * }
     * }</pre>
     */
    private void initDataSource() {
        // TODO: 初始化 H2 数据源 + 创建示例数据
        throw new UnsupportedOperationException("TODO: 初始化数据源");
    }

    // ==========================================================
    // 工具注册
    // ==========================================================

    private void registerTools() {
        // 工具1：执行 SQL 查询
        registerTool("query_database",
                "执行只读SQL查询（仅允许SELECT语句），返回JSON格式的结果集",
                SchemaFactory.object()
                        .add("sql", SchemaFactory.string("要执行的SELECT查询语句"))
                        .required("sql")
                        .build(),
                this::executeQuery
        );

        // 工具2：列出所有表
        registerTool("list_tables",
                "列出数据库中的所有表及其行数",
                SchemaFactory.object().build(),
                this::listTables
        );

        // 工具3：查看表结构
        registerTool("describe_table",
                "查看指定表的结构（列名、类型、是否可空）",
                SchemaFactory.object()
                        .add("table_name", SchemaFactory.string("要查看的表名"))
                        .required("table_name")
                        .build(),
                this::describeTable
        );
    }

    // ==========================================================
    // 工具实现（你来写）
    // ==========================================================

    /**
     * TODO: 执行 SELECT 查询并返回 JSON 结果集。
     *
     * <h3>安全检查（必须做）：</h3>
     * <ul>
     *   <li>只允许 SELECT 语句</li>
     *   <li>禁止 DROP / DELETE / INSERT / UPDATE / TRUNCATE / ALTER / CREATE</li>
     *   <li>SQL 关键字用大写判断</li>
     * </ul>
     *
     * <h3>返回格式：</h3>
     * <pre>{@code
     * {
     *   "columns": ["id", "name", "email"],
     *   "rows": [
     *     {"id": 1, "name": "张三", "email": "zhangsan@example.com"},
     *     {"id": 2, "name": "李四", "email": "lisi@example.com"}
     *   ],
     *   "row_count": 2
     * }
     * }</pre>
     */
    private JsonNode executeQuery(JsonNode arguments) throws Exception {
        // TODO: 1. 获取 sql 参数  2. 安全检查  3. 执行查询  4. 构建 JSON 返回
        throw new UnsupportedOperationException("TODO: 实现 SQL 查询执行");
    }

    /**
     * TODO: 列出所有表及其行数。
     *
     * <p>H2 中查询所有表：
     * <pre>{@code
     * SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
     * WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'TABLE'
     * }</pre>
     */
    private JsonNode listTables(JsonNode arguments) throws Exception {
        // TODO: 查询所有用户表，返回表名和行数
        throw new UnsupportedOperationException("TODO: 实现 listTables");
    }

    /**
     * TODO: 查看指定表的结构。
     *
     * <p>H2 中查询列信息：
     * <pre>{@code
     * SELECT COLUMN_NAME, TYPE_NAME, NULLABLE
     * FROM INFORMATION_SCHEMA.COLUMNS
     * WHERE TABLE_NAME = ?
     * }</pre>
     */
    private JsonNode describeTable(JsonNode arguments) throws Exception {
        // TODO: 实现 describeTable
        throw new UnsupportedOperationException("TODO: 实现 describeTable");
    }

    // ===== 入口 =====

    /**
     * TODO: MCP Server 启动入口。
     * <pre>{@code
     * new DatabaseMcpServer().start();
     * }</pre>
     */
    public static void main(String[] args) {
        // TODO: 启动 DatabaseMcpServer
        throw new UnsupportedOperationException("TODO: 启动 DatabaseMcpServer");
    }
}

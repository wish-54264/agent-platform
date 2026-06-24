package com.agent.mcp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP Session 管理器 — 管理多个 MCP Server 的连接生命周期。
 * <p>
 * 在 Agent 启动时初始化所有 MCP Server 连接，
 * Agent 运行时直接从 Manager 获取对应的 Session 进行工具调用。
 *
 * <h3>你需要实现的内容：</h3>
 * <ol>
 *   <li>{@link #registerServer(String, String...)} — 注册一个 MCP Server</li>
 *   <li>{@link #initAll()} — 启动所有已注册的 Server</li>
 *   <li>{@link #getSession(String)} — 按名称获取 Session</li>
 *   <li>{@link #shutdownAll()} — 关闭所有 Session</li>
 * </ol>
 */
public class McpSessionManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** 所有注册的 Session：serverName → McpSession */
    private final Map<String, McpSession> sessions = new ConcurrentHashMap<>();

    /** 未启动的 Server 注册信息 */
    private final Map<String, String[]> pendingServers = new ConcurrentHashMap<>();

    /**
     * TODO: 注册一个 MCP Server（不立即启动）。
     *
     * @param serverName Server 名称（如 "database"、"http-api"）
     * @param command    启动命令（如 {"java", "-cp", "xxx.jar", "com.agent.mcp.servers.DatabaseMcpServer"}）
     */
    public void registerServer(String serverName, String... command) {
        // TODO: 保存注册信息，等 initAll() 时统一启动
        throw new UnsupportedOperationException("TODO: 实现 registerServer");
    }

    /**
     * TODO: 启动所有已注册的 MCP Server。
     *
     * <p>启动后对每个 Server 调用 fetchTools() 获取工具列表。
     * 如果某个 Server 启动失败，记录日志但不阻塞其他 Server 的启动。
     */
    public void initAll() {
        // TODO: 遍历 pendingServers，创建 McpSession，调用 start() + fetchTools()
        throw new UnsupportedOperationException("TODO: 实现 initAll");
    }

    /**
     * TODO: 根据名称获取 Session。
     *
     * @param serverName Server 名称
     * @return 对应的 McpSession
     * @throws IllegalStateException 如果 Server 未找到或未启动
     */
    public McpSession getSession(String serverName) {
        // TODO: 从 sessions Map 中获取，不存在则抛异常
        throw new UnsupportedOperationException("TODO: 实现 getSession");
    }

    /**
     * TODO: 获取所有已连接的 MCP Server 名称。
     */
    public Map<String, McpSession> getAllSessions() {
        return Map.copyOf(sessions);
    }

    /**
     * TODO: 关闭所有 Session，释放资源。
     *
     * <p>在 Spring Boot 关闭时调用（@PreDestroy）。
     */
    public void shutdownAll() {
        // TODO: 遍历所有 session 调用 close()
        throw new UnsupportedOperationException("TODO: 实现 shutdownAll");
    }
}

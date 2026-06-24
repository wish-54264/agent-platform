package com.agent.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Agent Platform 启动入口。
 * <p>
 * 启动前请确保：
 * <ol>
 *   <li>docker-compose up -d（启动 PGVector + Redis）</li>
 *   <li>设置环境变量：
 *     <ul>
 *       <li>{@code BAILIAN_API_KEY} — 阿里云百炼 API Key</li>
 *       <li>{@code DB_URL} — PostgreSQL 连接 URL（默认 jdbc:postgresql://localhost:5432/agent_platform）</li>
 *       <li>{@code DB_USERNAME} / {@code DB_PASSWORD} — 数据库凭据</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h3>启动后验证：</h3>
 * <pre>{@code
 * # 健康检查
 * curl http://localhost:8080/actuator/health
 *
 * # 流式对话
 * curl -N -X POST http://localhost:8080/api/chat/stream \
 *   -H "Content-Type: application/json" \
 *   -d '{"message": "你好，帮我查一下数据库有哪些表", "conversationId": null}'
 * }</pre>
 */
@SpringBootApplication
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }
}

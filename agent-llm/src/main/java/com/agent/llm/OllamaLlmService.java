package com.agent.llm;

import com.agent.common.model.ChatChunk;
import com.agent.common.model.Message;
import com.agent.common.model.ToolDefinition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Ollama 本地模型实现（用于开发调试，免费无网也能跑）。
 * <p>
 * Ollama API 端点：
 * <ul>
 *   <li>Chat:      {@code POST http://localhost:11434/api/chat}</li>
 *   <li>Embedding: {@code POST http://localhost:11434/api/embed}</li>
 * </ul>
 *
 * <h3>Ollama 对 FC 的支持：</h3>
 * Ollama 从 0.3.x 开始支持 OpenAI 兼容的 tool calling。
 * 建议使用 {@code qwen2.5:7b} 或更高版本，并设置 {@code "tool_choice": "auto"}。
 * 如果模型不支持 FC，需要做降级处理（手动文本解析）。
 *
 * <h3>你需要实现的内容：</h3>
 * <ol>
 *   <li>Ollama Chat API 的流式调用 + FC 工具调用处理</li>
 *   <li>Ollama Embedding API 调用</li>
 *   <li>FC 不支持的模型降级方案（可选）</li>
 * </ol>
 */
@Slf4j
public class OllamaLlmService implements LlmService {

    private final WebClient webClient;
    private final String model;  // 默认: qwen2.5:7b

    private static final String BASE_URL = "http://localhost:11434";
    private static final String CHAT_PATH = "/api/chat";
    private static final String EMBED_PATH = "/api/embed";

    public OllamaLlmService(String model) {
        this.model = model != null ? model : "qwen2.5:7b";
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * TODO: 实现 Ollama 流式 FC 调用。
     * <p>
     * Ollama Chat API 请求体格式（与 OpenAI 差异较大）：
     * <pre>{@code
     * {
     *   "model": "qwen2.5:7b",
     *   "messages": [{"role": "user", "content": "..."}],
     *   "tools": [...],     // Ollama 0.3+ 支持
     *   "stream": true
     * }
     * }</pre>
     *
     * <h3>关键差异：</h3>
     * Ollama 的 SSE 格式为每行一个完整的 JSON 对象（没有 "data: " 前缀），
     * 且 tool_call 的返回格式与 OpenAI 略有不同。
     */
    @Override
    public Flux<ChatChunk> chatStream(List<Message> messages, List<ToolDefinition> toolDefinitions) {
        // TODO: 实现 Ollama 流式 FC 调用
        throw new UnsupportedOperationException("TODO: 实现 Ollama 流式 FC 调用");
    }

    /**
     * TODO: 实现 Ollama 同步 FC 调用。
     */
    @Override
    public ChatResponse chatSync(List<Message> messages, List<ToolDefinition> toolDefinitions) {
        // TODO: 实现 Ollama 同步调用
        throw new UnsupportedOperationException("TODO: 实现 Ollama 同步调用");
    }

    /**
     * TODO: 实现 Ollama Embedding。
     * <p>
     * 需要先 {@code ollama pull nomic-embed-text}。
     * <pre>{@code
     * POST http://localhost:11434/api/embed
     * {
     *   "model": "nomic-embed-text",
     *   "input": "今天天气怎么样"
     * }
     * Response: {"embeddings": [[0.123, -0.456, ...]]}
     * }</pre>
     */
    @Override
    public float[] embed(String text) {
        // TODO: 实现 Ollama Embedding
        throw new UnsupportedOperationException("TODO: 实现 Ollama Embedding");
    }
}

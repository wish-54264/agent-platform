package com.agent.llm;

import com.agent.common.model.ChatChunk;
import com.agent.common.model.Message;
import com.agent.common.model.ToolCall;
import com.agent.common.model.ToolDefinition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 阿里云百炼 LLM 服务实现。
 * <p>
 * 百炼 API 兼容 OpenAI 格式，端点：
 * <ul>
 *   <li>Chat:     {@code POST https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}</li>
 *   <li>Embedding:{@code POST https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings}</li>
 * </ul>
 *
 * <h3>你需要实现的内容：</h3>
 * <ol>
 *   <li>SSE 流式解析 — 逐行读取 "data: {...}" 并转为 ChatChunk</li>
 *   <li>Tool Call 增量聚合 — 多个 delta chunk 拼接成完整的 JSON 参数</li>
 *   <li>错误处理 — HTTP 4xx/5xx → AgentException</li>
 *   <li>embed() — 调用百炼 Embedding API</li>
 * </ol>
 */
@Slf4j
public class BailianLlmService implements LlmService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiKey;
    private final String chatModel;       // 默认: qwen-plus
    private final String embeddingModel;  // 默认: text-embedding-v3

    // 百炼 API 基础 URL
    private static final String BASE_URL = "https://dashscope.aliyuncs.com";
    private static final String CHAT_PATH = "/compatible-mode/v1/chat/completions";
    private static final String EMBED_PATH = "/compatible-mode/v1/embeddings";

    public BailianLlmService(String apiKey, String chatModel, String embeddingModel) {
        this.apiKey = apiKey;
        this.chatModel = chatModel != null ? chatModel : "qwen-plus";
        this.embeddingModel = embeddingModel != null ? embeddingModel : "text-embedding-v3";
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // ==========================================================
    // chatStream — 流式 FC 调用（核心，你来写）
    // ==========================================================

    /**
     * TODO: 实现流式 FC 调用。
     *
     * <h3>实现步骤：</h3>
     * <ol>
     *   <li>将 messages + toolDefinitions 组装为 OpenAI FC 请求体 JSON</li>
     *   <li>设置 {@code "stream": true}</li>
     *   <li>通过 WebClient 发送 POST，获取 SSE 流</li>
     *   <li>逐行解析 "data: {...}" → ChatChunk</li>
     *   <li>处理 tool_call delta 的增量拼接</li>
     *   <li>检测 {@code finish_reason} 结束流</li>
     * </ol>
     *
     * <h3>请求体格式：</h3>
     * <pre>{@code
     * {
     *   "model": "qwen-plus",
     *   "messages": [...],
     *   "tools": [{"type": "function", "function": {"name": "...", ...}}],
     *   "tool_choice": "auto",
     *   "stream": true
     * }
     * }</pre>
     *
     * <h3>SSE 行格式：</h3>
     * <pre>{@code
     * data: {"choices":[{"delta":{"content":"你好"},"finish_reason":null}]}
     * data: {"choices":[{"delta":{"tool_calls":[{"index":0,"id":"call_xxx","function":{"name":"get_weather","arguments":"{\""}}]}}]}
     * data: [DONE]
     * }</pre>
     *
     * @see #buildRequestBody(List, List, boolean)  请求体构建
     * @see #parseSSELine(String)                    SSE 行解析
     */
    @Override
    public Flux<ChatChunk> chatStream(List<Message> messages, List<ToolDefinition> toolDefinitions) {
        // TODO: 实现流式 FC 调用
        //
        // 伪代码：
        // ObjectNode body = buildRequestBody(messages, toolDefinitions, true);
        // return webClient.post()
        //     .uri(CHAT_PATH)
        //     .bodyValue(body)
        //     .retrieve()
        //     .bodyToFlux(String.class)
        //     .filter(line -> line.startsWith("data: ") && !line.equals("data: [DONE]"))
        //     .map(this::parseSSELine)
        //     .flatMap(this::aggregateToolCalls);  // 处理增量拼接
        //
        throw new UnsupportedOperationException("TODO: 实现流式 FC 调用");
    }

    // ==========================================================
    // chatSync — 同步 FC 调用（调试用，你来写）
    // ==========================================================

    /**
     * TODO: 实现同步 FC 调用。
     * <p>
     * 与 chatStream 的区别：设置 {@code "stream": false}，
     * 等完整响应返回后一次解析出 content + tool_calls。
     */
    @Override
    public ChatResponse chatSync(List<Message> messages, List<ToolDefinition> toolDefinitions) {
        // TODO: 实现同步调用
        throw new UnsupportedOperationException("TODO: 实现同步 FC 调用");
    }

    // ==========================================================
    // embed — 文本向量化（你来写）
    // ==========================================================

    /**
     * TODO: 实现文本向量化。
     *
     * <h3>百炼 Embedding API：</h3>
     * <pre>{@code
     * POST https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings
     * {
     *   "model": "text-embedding-v3",
     *   "input": "今天天气怎么样"
     * }
     *
     * Response:
     * {
     *   "data": [{"embedding": [0.123, -0.456, ...]}],
     *   "usage": {"total_tokens": 5}
     * }
     * }</pre>
     *
     * @param text 待向量化的文本
     * @return 向量 float[]（text-embedding-v3: 1536 维）
     */
    @Override
    public float[] embed(String text){
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model",embeddingModel);
        body.put("input",text);
        try {
            String responseJson = webClient.post()
            .uri(EMBED_PATH)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");
            float[] vector = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = (float) embeddingNode.get(i).asDouble();
            }
            return vector;
        } catch (Exception e) {
            log.error("Embedding 失败",e);
            throw new RuntimeException("文本向量化失败: " + e.getMessage(), e);
        }
    }

    // ==========================================================
    // 以下是辅助方法
    // ==========================================================

    /**
     * 构建 OpenAI FC 兼容的请求体。
     *
     * @param messages  对话历史
     * @param tools     工具定义列表
     * @param stream    是否流式
     * @return JSON 请求体
     */
    private ObjectNode buildRequestBody(List<Message> messages,
                                         List<ToolDefinition> tools,
                                         boolean stream) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", chatModel);
        body.put("stream", stream);

        // 如果 tools 非空，设置 tool_choice
        if (tools != null && !tools.isEmpty()) {
            body.put("tool_choice", "auto");
        }

        // 构建 messages 数组
        ArrayNode messagesNode = body.putArray("messages");
        for (Message msg : messages) {
            ObjectNode msgNode = messagesNode.addObject();
            msgNode.put("role", msg.getRole());

            if (msg.getContent() != null) {
                msgNode.put("content", msg.getContent());
            }

            // tool_calls（assistant 消息）
            if (msg.getToolCalls() != null && !msg.getToolCalls().isEmpty()) {
                ArrayNode tcArray = msgNode.putArray("tool_calls");
                for (ToolCall tc : msg.getToolCalls()) {
                    ObjectNode tcNode = tcArray.addObject();
                    tcNode.put("id", tc.getId());
                    tcNode.put("type", "function");
                    ObjectNode funcNode = tcNode.putObject("function");
                    funcNode.put("name", tc.getName());
                    funcNode.put("arguments",
                            tc.getArguments() != null ? tc.getArguments().toString() : "{}");
                }
            }

            // tool_call_id（tool 消息）
            if (msg.getToolCallId() != null) {
                msgNode.put("tool_call_id", msg.getToolCallId());
            }
        }

        // 构建 tools 数组
        if (tools != null && !tools.isEmpty()) {
            ArrayNode toolsNode = body.putArray("tools");
            for (ToolDefinition td : tools) {
                ObjectNode toolNode = toolsNode.addObject();
                toolNode.put("type", "function");
                ObjectNode funcNode = toolNode.putObject("function");
                funcNode.put("name", td.getName());
                funcNode.put("description", td.getDescription());
                if (td.getParameters() != null) {
                    funcNode.set("parameters", td.getParameters());
                }
            }
        }

        return body;
    }

    /**
     * 解析一行 SSE data。
     *
     * @param sseLine 格式: {@code data: {"choices":[...]}}
     * @return ChatChunk（可能为 null，表示应跳过此行）
     */
    private ChatChunk parseSSELine(String sseLine) {
        // TODO: 解析 SSE 行，这是一个关键方法。
        // 提示：
        // 1. 去掉 "data: " 前缀
        // 2. JSON 解析得到 choices[0].delta
        // 3. 判断 delta 中是 content 还是 tool_calls
        // 4. 返回对应的 ChatChunk
        throw new UnsupportedOperationException("TODO: 解析 SSE 行");
    }

    /**
     * 聚合工具调用的增量 chunk。
     * <p>
     * 因为 streaming 模式下 tool_call.arguments 可能分多个 chunk 返回：
     * <pre>
     * chunk1: {"city": "
     * chunk2: 北京
     * chunk3: "}
     * </pre>
     * 需要用一个 Map<String, StringBuilder> 跟踪每个 tool_call_id 的参数字符串拼接进度。
     */
    private Flux<ChatChunk> aggregateToolCalls(Flux<ChatChunk> rawChunks) {
        // TODO: 实现 Tool Call 增量聚合。
        // 提示：
        // 1. 维护 Map<index, ToolCallAggregation> 状态
        // 2. TOOL_CALL_START → 记录 id 和 name
        // 3. TOOL_CALL_DELTA → 追加 argumentsDelta
        // 4. TOOL_CALL_END → 解析完整 JSON，构建 ToolCall 对象
        throw new UnsupportedOperationException("TODO: 实现 Tool Call 增量聚合");
    }
}

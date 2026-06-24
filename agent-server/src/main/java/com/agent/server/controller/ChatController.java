package com.agent.server.controller;

import com.agent.common.model.AgentEvent;
import com.agent.core.FcAgentEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 对话 API 控制器。
 * <p>
 * 提供流式（SSE）和同步两种对话接口。
 *
 * <h3>SSE 事件流示例（前端收到的）：</h3>
 * <pre>
 * event: thinking
 * data: {"type":"THINKING","content":"好的，我先查一下...","round":1}
 *
 * event: tool_call
 * data: {"type":"TOOL_CALL","toolCallId":"call_001","toolName":"database:list_tables","toolArguments":{},"round":1}
 *
 * event: tool_result
 * data: {"type":"TOOL_RESULT","toolCallId":"call_001","success":true,"toolResult":{...},"round":1}
 *
 * event: answer
 * data: {"type":"ANSWER","content":"数据库中有以下表：users, orders...","round":2}
 *
 * event: done
 * data: {"type":"DONE","totalRounds":2,"totalTokens":450}
 * </pre>
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #chatStream(ChatRequest)} — SSE 流式对话接口
 * {@link #chatSync(ChatRequest)} — 同步对话接口（调试用）
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final FcAgentEngine agentEngine;

    /**
     * 流式对话请求体。
     */
    public record ChatRequest(
            String message,         // 用户输入
            String conversationId   // 会话 ID（null 则创建新会话）
    ) {}

    /**
     * TODO: SSE 流式对话接口。
     *
     * <p>这是用户直接调用的主接口。Agent 引擎返回的每个
     * {@link AgentEvent} 被封装为 {@link ServerSentEvent} 推送给前端。
     *
     * <h3>实现要点：</h3>
     * <ul>
     *   <li>如果 conversationId 为空 → 创建新会话</li>
     *   <li>调用 agentEngine.executeStream(message, conversationId)</li>
     *   <li>将 AgentEvent 映射为 ServerSentEvent：
     *     <ul>
     *       <li>event 名 = AgentEvent.type 的小写形式</li>
     *       <li>data = AgentEvent 的 JSON 序列化</li>
     *     </ul>
     *   </li>
     *   <li>异常处理：捕获异常 → 推送 error 事件</li>
     * </ul>
     *
     * <pre>{@code
     * @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
     * public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {
     *     String convId = request.conversationId() != null
     *         ? request.conversationId()
     *         : conversationStore.createConversation("新对话", "qwen-plus");
     *
     *     return agentEngine.executeStream(request.message(), convId)
     *         .map(event -> ServerSentEvent.<String>builder()
     *             .event(event.getType().name().toLowerCase())
     *             .data(objectMapper.writeValueAsString(event))
     *             .build())
     *         .onErrorResume(e -> {
     *             log.error("Agent 执行异常", e);
     *             return Flux.just(ServerSentEvent.<String>builder()
     *                 .event("error")
     *                 .data("{\"error\": \"" + e.getMessage() + "\"}")
     *                 .build());
     *         });
     * }
     * }</pre>
     */
    @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {
        // TODO: 实现 SSE 流式对话接口
        throw new UnsupportedOperationException("TODO: 实现流式对话接口");
    }

    /**
     * TODO: 同步对话接口（调试用）。
     *
     * <p>等 Agent 引擎执行完所有轮次后一次性返回结果。
     * 前端可以用这个接口做快速调试，不需要处理 SSE 流。
     */
    @PostMapping
    public Map<String, Object> chatSync(@RequestBody ChatRequest request) {
        // TODO: 实现同步对话接口
        // 提示：收集 executeStream 的所有事件，提取最后的 answer
        throw new UnsupportedOperationException("TODO: 实现同步对话接口");
    }
}

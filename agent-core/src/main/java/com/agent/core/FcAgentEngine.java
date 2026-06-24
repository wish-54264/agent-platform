package com.agent.core;

import com.agent.common.model.AgentEvent;
import com.agent.common.model.ChatChunk;
import com.agent.common.model.Message;
import com.agent.common.model.ToolCall;
import com.agent.common.model.ToolDefinition;
import com.agent.llm.LlmService;
import com.agent.mcp.integration.McpToolRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FC 原生 Agent 引擎 —— 整个项目的核心。
 *
 * <h3>核心流程（FC 模式 ReAct 循环）：</h3>
 * <pre>
 * 用户输入
 *   │
 *   ▼
 * ┌─────────────────────────────────────────────┐
 * │ 第 N 轮:                                     │
 * │  1. 组装 FC 请求 (messages + tools schema)    │
 * │  2. 流式调用 LLM                              │
 * │  3. 聚合响应: 文本 → thinking 事件             │
 * │              tool_calls → tool_call 事件      │
 * │  4. 如果有 tool_calls:                        │
 * │     → 执行工具 (MCP 协议)                     │
 * │     → 推送 tool_result 事件                   │
 * │     → 将 assistant + tool 消息写入历史        │
 * │     → 回到第1步                               │
 * │  5. 如果没有 tool_calls:                      │
 * │     → 推送 answer 事件                        │
 * │     → 推送 done 事件                          │
 * │     → 结束                                    │
 * └─────────────────────────────────────────────┘
 * </pre>
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #executeStream(String, String)} — 整个 Agent 循环的流式执行
 *
 * <h3>实现难点提示：</h3>
 * <ul>
 *   <li><b>FC 流式聚合</b>：LLM 的流式响应中 tool_call 的 arguments 是分片到达的，
 *       需要用 Map&lt;Integer, ToolCallBuilder&gt; 跟踪每个 tool_call 的增量构建进度</li>
 *   <li><b>并行工具调用</b>：LLM 可能一次返回多个 tool_call，它们之间无依赖时可以并行执行</li>
 *   <li><b>历史追加时机</b>：每轮结束后把 assistant(tool_calls) + tool(result) 成对写入历史</li>
 *   <li><b>循环终止条件</b>：LLM 不再返回 tool_calls 且 finish_reason="stop"</li>
 * </ul>
 */
@Slf4j
public class FcAgentEngine {

    private final LlmService llmService;
    private final McpToolRegistry toolRegistry;
    private final ConversationStore conversationStore;
    private final PromptBuilder promptBuilder;

    /** 最大循环轮次（防止死循环） */
    private static final int MAX_ROUNDS = 10;

    public FcAgentEngine(LlmService llmService,
                         McpToolRegistry toolRegistry,
                         ConversationStore conversationStore) {
        this.llmService = llmService;
        this.toolRegistry = toolRegistry;
        this.conversationStore = conversationStore;
        this.promptBuilder = new PromptBuilder();
    }

    // ==========================================================
    // 流式 Agent 循环（核心，你来写）
    // ==========================================================

    /**
     * TODO: 流式执行 Agent 循环。
     *
     * <h3>这是一个递归 Flux 模式：</h3>
     * <pre>{@code
     * Flux.defer(() -> {
     *     // 1. 加载历史消息
     *     List<Message> history = conversationStore.load(conversationId);
     *     history.add(Message.user(userMessage));
     *
     *     // 2. 调用递归循环
     *     return executeLoop(history, new AtomicInteger(0), conversationId);
     * });
     * }</pre>
     *
     * <h3>{@code executeLoop} 内部逻辑：</h3>
     * <ol>
     *   <li>检查轮次：round > MAX_ROUNDS → 返回 error 事件</li>
     *   <li>获取工具定义列表：toolRegistry.getToolDefinitions()</li>
     *   <li>组装 System Prompt：promptBuilder.build(toolDefinitions)</li>
     *   <li>构建 messages 列表（system prompt + history）</li>
     *   <li>调用 llmService.chatStream(messages, toolDefinitions)</li>
     *   <li>处理流式 chunk：
     *     <ul>
     *       <li>CONTENT → 推送 thinking 事件</li>
     *       <li>TOOL_CALL_DELTA → 增量聚合，不推送</li>
     *       <li>TOOL_CALL_END → 推送 tool_call 事件 + 执行工具</li>
     *       <li>FINISH → 检查 finishReason</li>
     *     </ul>
     *   </li>
     *   <li>工具执行完成后：推送 tool_result → 追加历史 → 递归下一轮</li>
     *   <li>finish_reason="stop" → 推送 answer → 推送 done → 结束</li>
     * </ol>
     *
     * <h3>关于递归 Flux：</h3>
     * 使用 {@code Flux.concat(a, Flux.defer(() -> executeLoop(...)))} 实现：
     * - 先发出当前轮次的所有事件（thinking → tool_call → tool_result）
     * - 然后递归发出下一轮的事件
     * - 当没有更多 tool_calls 时，发出 answer + done 后结束（Flux.empty()）
     *
     * @param userMessage     用户最新输入
     * @param conversationId  会话 ID
     * @return AgentEvent 事件流
     */
    public Flux<AgentEvent> executeStream(String userMessage, String conversationId) {
        // TODO: 实现流式 Agent 循环
        //
        // ===== 伪代码框架 =====
        //
        // return Flux.defer(() -> {
        //     List<Message> history = conversationStore.load(conversationId);
        //     history.add(Message.user(userMessage));
        //     return executeLoop(history, new AtomicInteger(1), conversationId);
        // });
        //
        // ---
        //
        // private Flux<AgentEvent> executeLoop(List<Message> history,
        //                                       AtomicInteger round,
        //                                       String conversationId) {
        //     if (round.get() > MAX_ROUNDS) {
        //         return Flux.just(AgentEvent.error(round.get(), "超过最大循环轮次"));
        //     }
        //
        //     List<ToolDefinition> tools = toolRegistry.getToolDefinitions();
        //     List<Message> messages = buildMessagesList(history, tools);
        //
        //     return llmService.chatStream(messages, tools)
        //         .collectList()  // 先收集所有 chunk（或使用更复杂的 flatMap）
        //         .flatMapMany(chunks -> {
        //             // 聚合：区分 content 和 tool_calls
        //             String content = aggregateContent(chunks);
        //             List<ToolCall> toolCalls = aggregateToolCalls(chunks);
        //             String finishReason = getFinishReason(chunks);
        //
        //             Flux<AgentEvent> currentRoundEvents = Flux.empty();
        //
        //             // 推送 content 作为 thinking 事件
        //             if (content != null && !content.isEmpty()) {
        //                 currentRoundEvents = Flux.concat(currentRoundEvents,
        //                     Flux.just(AgentEvent.thinking(round.get(), content)));
        //             }
        //
        //             // 如果有工具调用 → 执行工具 → 递归下一轮
        //             if (!toolCalls.isEmpty()) {
        //                 return Flux.concat(
        //                     currentRoundEvents,
        //                     Flux.fromIterable(toolCalls).flatMap(tc -> {
        //                         // 推送 tool_call 事件
        //                         Flux<AgentEvent> callEvent = Flux.just(
        //                             AgentEvent.toolCall(round.get(), tc.getId(), tc.getName(), tc.getArguments()));
        //                         // 执行工具 → 推送结果
        //                         return Flux.concat(callEvent, executeAndPushResult(tc, round));
        //                     }),
        //                     // 追加历史 → 递归
        //                     Flux.defer(() -> {
        //                         history.add(Message.assistantWithToolCalls(toolCalls));
        //                         for (ToolCall tc : toolCalls) {
        //                             history.add(Message.tool(tc.getId(), tc.getName(), toolResultCache.get(tc.getId())));
        //                         }
        //                         return executeLoop(history, new AtomicInteger(round.get() + 1), conversationId);
        //                     })
        //                 );
        //             }
        //
        //             // 没有工具调用 → 最终回答
        //             history.add(Message.assistant(content));
        //             conversationStore.save(conversationId, history);
        //             return Flux.concat(
        //                 currentRoundEvents,
        //                 Flux.just(AgentEvent.answer(round.get(), content)),
        //                 Flux.just(AgentEvent.done(round.get(), 0))
        //             );
        //         });
        // }
        //
        throw new UnsupportedOperationException("TODO: 实现 Agent 循环引擎 — 这是整个项目的核心！");
    }

    // ==========================================================
    // 工具执行（你来写）
    // ==========================================================

    /**
     * TODO: 执行单个工具调用并推送结果事件。
     *
     * <p>调用 {@link McpToolRegistry#execute(String, JsonNode)}，
     * 成功后推送 TOOL_RESULT 事件。
     * 失败时推送 ERROR 事件（不终止循环，让 LLM 自行决定如何处理失败）。
     *
     * @param toolCall 工具调用信息
     * @param round    当前轮次
     * @return TOOL_RESULT 或 ERROR 事件
     */
    private Flux<AgentEvent> executeAndPushResult(ToolCall toolCall, AtomicInteger round) {
        // TODO: 实现工具执行 + 结果推送
        // try {
        //     JsonNode result = toolRegistry.execute(toolCall.getName(), toolCall.getArguments());
        //     return Flux.just(AgentEvent.toolResult(round.get(), toolCall.getId(), true, result));
        // } catch (Exception e) {
        //     return Flux.just(AgentEvent.toolResult(round.get(), toolCall.getId(), false, e.getMessage()));
        // }
        throw new UnsupportedOperationException("TODO: 实现工具执行");
    }

    // ==========================================================
    // 辅助方法
    // ==========================================================

    /**
     * 构建发送给 LLM 的 messages 列表（system prompt + 历史 + 工具定义）。
     */
    private List<Message> buildMessagesList(List<Message> history, List<ToolDefinition> tools) {
        List<Message> messages = new ArrayList<>();
        // 第一条：system prompt（包含工具使用规则）
        messages.add(Message.system(promptBuilder.build(tools)));
        // 后续：完整的对话历史
        messages.addAll(history);
        return messages;
    }
}

package com.agent.llm;

import com.agent.common.model.ChatChunk;
import com.agent.common.model.Message;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * LLM 服务统一接口。
 * <p>
 * 所有 LLM 提供商（百炼、Ollama、OpenAI）都实现此接口，
 * 业务层只依赖接口，切换模型零代码改动。
 *
 * <h3>使用方式</h3>
 * <pre>{@code
 * // 流式 FC 调用（Agent 核心依赖）
 * Flux<ChatChunk> stream = llmService.chatStream(tools, messages);
 *
 * // 同步调用（调试用）
 * String answer = llmService.chatSync(tools, messages);
 *
 * // 向量化（RAG 依赖）
 * float[] vec = llmService.embed("今天天气怎么样");
 * }</pre>
 */
public interface LlmService {

    /**
     * FC 原生流式调用（Agent 核心方法）。
     * <p>
     * 返回 SSE 事件流的 Flux，每个 ChatChunk 可能是文本片段或工具调用片段。
     * 调用方负责聚合 ChatChunk，构建完整的 assistant 消息。
     *
     * @param toolDefinitions 可用工具列表（空列表 = 纯对话模式）
     * @param messages        完整对话历史（含 system prompt）
     * @return 流式响应
     */
    Flux<ChatChunk> chatStream(List<Message> messages,
                               List<com.agent.common.model.ToolDefinition> toolDefinitions);

    /**
     * FC 同步调用（调试 / 简单场景）。
     *
     * @param toolDefinitions 可用工具列表
     * @param messages        完整对话历史
     * @return 完整响应（含文本 + 工具调用）
     */
    ChatResponse chatSync(List<Message> messages,
                          List<com.agent.common.model.ToolDefinition> toolDefinitions);

    /**
     * 文本向量化（RAG 依赖）。
     *
     * @param text 待向量化的文本
     * @return 向量（维度取决于模型）
     */
    float[] embed(String text);

    // ===== 内部数据类型 =====

    /**
     * 同步调用的返回结果。
     */
    record ChatResponse(
            String content,
            List<com.agent.common.model.ToolCall> toolCalls,
            TokenUsage usage
    ) {}

    record TokenUsage(int promptTokens, int completionTokens, int totalTokens) {}
}

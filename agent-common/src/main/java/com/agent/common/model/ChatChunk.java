package com.agent.common.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * LLM 流式响应的单个 chunk。
 * <p>
 * FC 模式下 LLM 的流式返回中，一个 chunk 可能是：
 * <ul>
 *   <li>文本片段（content delta）</li>
 *   <li>工具调用片段（tool_call delta，增量构建）</li>
 *   <li>结束标记（finish_reason = "tool_calls" 或 "stop"）</li>
 * </ul>
 * <p>
 * 调用方需要将多个 chunk 中的 arguments 增量拼接，在 TOOL_CALL_END 时
 * 得到完整的 JSON 参数字符串。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatChunk {

    /** Chunk 类型 */
    private ChunkType type;

    /** 文本内容片段（CONTENT 类型时使用） */
    private String content;

    /** 工具调用 ID */
    private String toolCallId;

    /** 工具名称 */
    private String toolName;

    /** 增量 JSON 参数字符串（已拼接好的部分） */
    private String argumentsDelta;

    /** 本轮所有工具调用（TOOL_CALL_END 时汇总） */
    @Builder.Default
    private List<ToolCall> allToolCalls = new ArrayList<>();

    /** 结束原因：stop / tool_calls / length */
    private String finishReason;

    /** 本轮 usage（仅最后一个 chunk 有值） */
    private Usage usage;

    public enum ChunkType {
        /** 文本内容片段 */
        CONTENT,
        /** 工具调用开始 */
        TOOL_CALL_START,
        /** 工具调用参数增量 */
        TOOL_CALL_DELTA,
        /** 工具调用结束（参数完整） */
        TOOL_CALL_END,
        /** 流结束 */
        FINISH
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}

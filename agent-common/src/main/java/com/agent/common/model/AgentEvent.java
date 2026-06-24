package com.agent.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSE 推送事件 — Agent 引擎每完成一个步骤就推一个事件给前端。
 * <p>
 * 事件流序列：
 * <ol>
 *   <li>THINKING    — LLM 思考过程（流式逐字推送）</li>
 *   <li>TOOL_CALL   — 决定调用工具</li>
 *   <li>TOOL_RESULT — 工具执行结果</li>
 *   <li>... （可能多轮 THINKING → TOOL_CALL → TOOL_RESULT）</li>
 *   <li>ANSWER      — 最终答案</li>
 *   <li>DONE        — 对话结束（含统计信息）</li>
 * </ol>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentEvent {

    private EventType type;

    /** 文本内容（THINKING / ANSWER / ERROR 时使用） */
    private String content;

    /** 工具调用 ID（TOOL_CALL / TOOL_RESULT 时使用） */
    private String toolCallId;

    /** 工具名称（TOOL_CALL 时使用） */
    private String toolName;

    /** 工具参数（TOOL_CALL 时使用） */
    private Object toolArguments;

    /** 工具执行结果（TOOL_RESULT 时使用） */
    private Object toolResult;

    /** 工具调用是否成功（TOOL_RESULT 时使用） */
    private Boolean success;

    /** 当前轮次 */
    private Integer round;

    /** 总轮次（DONE 时使用） */
    private Integer totalRounds;

    /** Token 消耗（DONE 时使用） */
    private Integer totalTokens;

    // ===== 工厂方法 =====

    public static AgentEvent thinking(int round, String content) {
        return AgentEvent.builder()
                .type(EventType.THINKING)
                .round(round)
                .content(content)
                .build();
    }

    public static AgentEvent toolCall(int round, String toolCallId, String toolName, Object arguments) {
        return AgentEvent.builder()
                .type(EventType.TOOL_CALL)
                .round(round)
                .toolCallId(toolCallId)
                .toolName(toolName)
                .toolArguments(arguments)
                .build();
    }

    public static AgentEvent toolResult(int round, String toolCallId, boolean success, Object result) {
        return AgentEvent.builder()
                .type(EventType.TOOL_RESULT)
                .round(round)
                .toolCallId(toolCallId)
                .success(success)
                .toolResult(result)
                .build();
    }

    public static AgentEvent answer(int round, String content) {
        return AgentEvent.builder()
                .type(EventType.ANSWER)
                .round(round)
                .content(content)
                .build();
    }

    public static AgentEvent done(int totalRounds, int totalTokens) {
        return AgentEvent.builder()
                .type(EventType.DONE)
                .totalRounds(totalRounds)
                .totalTokens(totalTokens)
                .build();
    }

    public static AgentEvent error(int round, String errorMessage) {
        return AgentEvent.builder()
                .type(EventType.ERROR)
                .round(round)
                .content(errorMessage)
                .build();
    }

    public enum EventType {
        THINKING,
        TOOL_CALL,
        TOOL_RESULT,
        ANSWER,
        DONE,
        ERROR
    }
}

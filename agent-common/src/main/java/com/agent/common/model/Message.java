package com.agent.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话消息 — 兼容 OpenAI Function Calling 格式。
 * <p>
 * role 取值：
 * <ul>
 *   <li>{@code system}  — 系统指令</li>
 *   <li>{@code user}    — 用户输入</li>
 *   <li>{@code assistant} — LLM 响应（可能包含 tool_calls）</li>
 *   <li>{@code tool}    — 工具执行结果</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    /** 角色：system / user / assistant / tool */
    private String role;

    /** 文本内容（user 输入、assistant 文字回复、tool 返回内容） */
    private String content;

    /** 工具名称（仅 role=tool 时使用，也可省略） */
    private String name;

    /** assistant 发起的工具调用列表（FC 原生格式） */
    private List<ToolCall> toolCalls;

    /** 工具调用 ID（仅 role=tool 时使用，与 ToolCall.id 对应） */
    private String toolCallId;

    // ===== 工厂方法 =====

    public static Message system(String content) {
        return Message.builder().role("system").content(content).build();
    }

    public static Message user(String content) {
        return Message.builder().role("user").content(content).build();
    }

    public static Message assistant(String content) {
        return Message.builder().role("assistant").content(content).build();
    }

    public static Message assistantWithToolCalls(List<ToolCall> toolCalls) {
        return Message.builder()
                .role("assistant")
                .content(null)
                .toolCalls(toolCalls)
                .build();
    }

    public static Message tool(String toolCallId, String toolName, String content) {
        return Message.builder()
                .role("tool")
                .toolCallId(toolCallId)
                .name(toolName)
                .content(content)
                .build();
    }
}

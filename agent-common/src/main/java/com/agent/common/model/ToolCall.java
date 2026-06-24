package com.agent.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM 返回的工具调用（FC 原生格式）。
 * <p>
 * 一条 assistant 消息可以包含多个 ToolCall，
 * 在 FC 模式下 LLM 自行决定并行调用多个工具。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolCall {

    /** 工具调用唯一 ID（LLM 生成，如 "call_xxxx"） */
    private String id;

    /** 工具名称 */
    private String name;

    /** 工具参数（JSON 对象） */
    private JsonNode arguments;
}

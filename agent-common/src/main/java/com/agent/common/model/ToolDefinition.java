package com.agent.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具定义 — 描述一个可用工具的名称、功能和参数 Schema。
 * <p>
 * 此对象会被序列化为 FC 原生格式的 tools 数组发送给 LLM：
 * <pre>{@code
 * {
 *   "type": "function",
 *   "function": {
 *     "name": "get_weather",
 *     "description": "获取指定城市的天气",
 *     "parameters": { ... JSON Schema ... }
 *   }
 * }
 * }</pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDefinition {

    /** 工具唯一名称（LLM 通过此名称选择工具） */
    private String name;

    /** 工具功能描述（越清晰 LLM 越不容易选错） */
    private String description;

    /** 参数 JSON Schema */
    private JsonNode parameters;
}

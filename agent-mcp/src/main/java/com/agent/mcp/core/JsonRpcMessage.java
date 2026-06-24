package com.agent.mcp.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON-RPC 2.0 消息模型。
 * <p>
 * 支持三种类型：
 * <ul>
 *   <li>Request  — 请求（method + params + id）</li>
 *   <li>Response — 成功响应（result + id）</li>
 *   <li>Error    — 错误响应（error + id）</li>
 * </ul>
 *
 * <h3>协议规范：</h3>
 * <pre>{@code
 * // 请求
 * {"jsonrpc": "2.0", "method": "tools/call", "params": {...}, "id": "1"}
 *
 * // 成功响应
 * {"jsonrpc": "2.0", "result": {...}, "id": "1"}
 *
 * // 错误响应
 * {"jsonrpc": "2.0", "error": {"code": -32600, "message": "Invalid Request"}, "id": "1"}
 * }</pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcMessage {

    /** 协议版本，固定 "2.0" */
    private String jsonrpc;

    /** 方法名（Request 专用） */
    private String method;

    /** 参数（Request 专用） */
    private JsonNode params;

    /** 成功结果（Response 专用） */
    private JsonNode result;

    /** 错误对象（Error Response 专用） */
    private JsonRpcError error;

    /** 请求 ID（Request / Response 都有） */
    private String id;

    // ===== 工厂方法 =====

    public static JsonRpcMessage request(String method, JsonNode params, String id) {
        return JsonRpcMessage.builder()
                .jsonrpc("2.0")
                .method(method)
                .params(params)
                .id(id)
                .build();
    }

    public static JsonRpcMessage success(String id, JsonNode result) {
        return JsonRpcMessage.builder()
                .jsonrpc("2.0")
                .result(result)
                .id(id)
                .build();
    }

    public static JsonRpcMessage error(String id, int code, String message) {
        return JsonRpcMessage.builder()
                .jsonrpc("2.0")
                .error(new JsonRpcError(code, message))
                .id(id)
                .build();
    }

    public boolean isRequest() {
        return method != null;
    }

    public boolean isError() {
        return error != null;
    }

    // ===== 内部类 =====

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonRpcError {
        private int code;
        private String message;
    }

    // ===== MCP 协议定义的标准错误码 =====

    /** 解析错误 */
    public static final int PARSE_ERROR = -32700;
    /** 无效请求 */
    public static final int INVALID_REQUEST = -32600;
    /** 方法不存在 */
    public static final int METHOD_NOT_FOUND = -32601;
    /** 参数无效 */
    public static final int INVALID_PARAMS = -32602;
    /** 内部错误 */
    public static final int INTERNAL_ERROR = -32603;
    /** 工具执行失败（MCP 自定义） */
    public static final int TOOL_EXECUTION_ERROR = -32000;
}

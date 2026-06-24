package com.agent.common.exception;

import lombok.Getter;

/**
 * Agent 平台统一异常基类。
 */
@Getter
public class AgentException extends RuntimeException {

    private final ErrorCode errorCode;

    public AgentException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AgentException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        // LLM 相关
        LLM_TIMEOUT,
        LLM_RATE_LIMITED,
        LLM_INVALID_RESPONSE,
        LLM_TOKEN_EXCEEDED,

        // Agent 相关
        AGENT_LOOP_EXCEEDED,
        AGENT_TOOL_NOT_FOUND,
        AGENT_TOOL_EXECUTION_FAILED,
        AGENT_SCHEMA_VALIDATION_FAILED,

        // MCP 相关
        MCP_CONNECTION_FAILED,
        MCP_SERVER_NOT_FOUND,
        MCP_PROTOCOL_ERROR,

        // RAG 相关
        RAG_DOCUMENT_PARSE_FAILED,
        RAG_EMBEDDING_FAILED,
        RAG_SEARCH_FAILED,
        RAG_OCR_FAILED,

        // 会话相关
        CONVERSATION_NOT_FOUND,
        CONVERSATION_TOO_LONG,

        // 通用
        CONFIGURATION_ERROR,
        INTERNAL_ERROR
    }
}

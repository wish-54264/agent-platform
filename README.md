# Agent Platform — AI 智能体平台

基于 **Java 21 + Spring Boot 3.3 + MCP 协议 + RAG 知识库** 的 AI Agent 平台。

## 架构

```
┌─────────────────────────────────┐
│     前端 (web/)                  │
│     流式对话 + 知识库管理         │
└──────────────┬──────────────────┘
               │ SSE / REST
┌──────────────▼──────────────────┐
│   agent-server (Spring Boot)    │
│   ChatController / KnowledgeCtl  │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│   agent-core (Agent 引擎)       │
│   FcAgentEngine — ReAct 循环    │
│   ConversationStore / Validator │
└───┬──────────┬──────────┬───────┘
    │          │          │
┌───▼───┐ ┌───▼───┐ ┌───▼─────────┐
│ LLM   │ │ MCP   │ │ RAG 管道     │
│ 网关   │ │ 协议   │ │ 文档→切分    │
│ 百炼   │ │ stdio  │ │ →向量→检索   │
│ Ollama│ │ 子进程  │ │ →重排        │
└───────┘ └──┬─────┘ └───┬─────────┘
             │           │
        ┌────▼────┐  ┌──▼──────────┐
        │Database │  │PGVector     │
        │ HTTP API│  │百炼 Rerank  │
        │ MCP Srv │  │百炼 OCR     │
        └─────────┘  └─────────────┘
```

## 技术栈

| 层 | 技术 |
|----|------|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.3 + WebFlux |
| AI 模型 | 阿里云百炼 (qwen-plus / text-embedding-v3 / gte-rerank) |
| 本地模型 | Ollama (qwen2.5:7b) |
| 向量数据库 | PostgreSQL 16 + PGVector |
| MCP 协议 | 自建 JSON-RPC 2.0 over stdio |
| 文档解析 | Apache Tika + 百炼 OCR |
| 数据库 | PostgreSQL |
| 缓存 | Redis |

## 快速开始

### 1. 环境准备

- Java 21+
- Maven 3.9+
- Docker Desktop
- 阿里云百炼 API Key（[免费申请](https://bailian.console.aliyun.com/)）

### 2. 启动基础设施

```bash
docker-compose up -d
```

### 3. 配置 API Key

```bash
# Windows PowerShell
$env:BAILIAN_API_KEY="your-api-key"

# Linux / macOS
export BAILIAN_API_KEY="your-api-key"
```

### 4. 编译启动

```bash
mvn clean compile -pl agent-server -am
mvn spring-boot:run -pl agent-server
```

### 5. 打开前端

浏览器打开 `web/index.html`

---

## 项目结构

```
agent-platform/
├── agent-common/        # 共享数据模型（Message, ToolCall, AgentEvent...）
├── agent-llm/           # LLM 网关（百炼 + Ollama）
├── agent-mcp/           # MCP 协议实现
│   ├── core/            #   JSON-RPC 编解码 + Server/Client
│   ├── servers/         #   DatabaseMcpServer, HttpApiMcpServer
│   └── integration/     #   McpToolRegistry
├── agent-core/          # Agent 核心引擎
│   └── FcAgentEngine    #   ReAct 循环（思考→行动→观察→递归）
├── agent-rag/           # RAG 知识库
│   ├── DocumentParser   #   Tika + OCR 文档解析
│   ├── ChunkSplitter    #   文本切分（512字/块，128字重叠）
│   ├── VectorStoreService # PGVector 向量存取
│   ├── RerankService    #   百炼 Cross-Encoder 精排
│   └── KnowledgeService #   RAG 管道编排
├── agent-server/        # Spring Boot 启动 + REST API
├── web/                 # 前端页面
├── scripts/             # init-db.sql
└── docker-compose.yml   # PGVector + Redis
```

## API 接口

### 流式对话

```bash
curl -N -X POST http://localhost:8080/api/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"message":"你好","conversationId":null}'
```

SSE 事件类型：`thinking` → `tool_call` → `tool_result` → `answer` → `done`

### 同步对话（调试用）

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好","conversationId":null}'
```

### 文档上传

```bash
curl -X POST http://localhost:8080/api/knowledge/upload \
  -F "file=@document.pdf"
```

### 知识库检索

```bash
curl "http://localhost:8080/api/knowledge/search?q=今天天气&topK=3"
```

## Agent 工作流程

```
用户输入
  │
  ▼
┌──────────────────────────┐
│ FcAgentEngine             │
│                           │
│ 1. 加载历史 + SystemPrompt│
│ 2. 流式调用LLM (百炼 FC)   │
│ 3. LLM 返回:              │
│    • 有 tool_calls → 执行 │
│    • 无 tool_calls → 回答 │
│ 4. 工具执行:               │
│    → MCP 协议 → 子进程     │
│    → JSON-RPC over stdio  │
│ 5. 工具结果追加到历史      │
│    → 递归下一轮            │
│ 6. 最终答案推送前端(SSE)   │
└──────────────────────────┘
```

## MCP 工具

| Server | 工具 | 说明 |
|--------|------|------|
| DatabaseMcpServer | `query_database` | 执行 SQL 查询（仅 SELECT） |
| | `list_tables` | 列出所有表 |
| | `describe_table` | 查看表结构 |
| HttpApiMcpServer | `http_get` | HTTP GET 请求 |
| | `http_post` | HTTP POST 请求 |

## 许可

MIT

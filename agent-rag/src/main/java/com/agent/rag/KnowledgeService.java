package com.agent.rag;

import com.agent.llm.LlmService;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 知识库服务 — RAG 管道的编排器。
 * <p>
 * 协调文档解析 → 切分 → 向量化 → 存储的全流程，
 * 以及检索时的向量检索 → 重排序 → 上下文拼接。
 *
 * <h3>你需要实现的内容：</h3>
 * <ol>
 *   <li>{@link #uploadDocument(Path, String)} — 文档上传全流程</li>
 *   <li>{@link #search(String, int)} — 知识库检索</li>
 * </ol>
 */
@Slf4j
public class KnowledgeService {

    private final DocumentParser documentParser;
    private final ChunkSplitter chunkSplitter;
    private final LlmService llmService;          // 用于向量化
    private final VectorStoreService vectorStore;
    private final RerankService rerankService;

    public KnowledgeService(DocumentParser documentParser,
                            ChunkSplitter chunkSplitter,
                            LlmService llmService,
                            VectorStoreService vectorStore,
                            RerankService rerankService) {
        this.documentParser = documentParser;
        this.chunkSplitter = chunkSplitter;
        this.llmService = llmService;
        this.vectorStore = vectorStore;
        this.rerankService = rerankService;
    }

    /**
     * TODO: 文档上传全流程。
     *
     * <h3>完整流程：</h3>
     * <pre>
     * 1. documentParser.parse(filePath) → 原始文本
     * 2. chunkSplitter.split(text) → 文本块列表
     * 3. 对每块调用 llmService.embed(chunk.text()) → 向量列表
     * 4. vectorStore.insertChunks(documentId, chunks, embeddings) → 写入 PGVector
     * 5. 更新 documents 表状态为 COMPLETED
     * </pre>
     *
     * <h3>注意：</h3>
     * <ul>
     *   <li>embed 调用是批量还是逐条？百炼 Embedding API 支持批量输入，
     *       但需要注意单次调用的 token 上限</li>
     *   <li>整个流程应异步执行（文档上传后立即返回，后台处理）</li>
     *   <li>异常处理：任何步骤失败都应更新 document 状态为 FAILED</li>
     * </ul>
     *
     * @param filePath 文档文件路径
     * @param fileName 原始文件名
     * @return 文档 ID
     */
    public UUID uploadDocument(Path filePath, String fileName) {
        // TODO: 实现文档上传全流程
        //
        // UUID documentId = UUID.randomUUID();
        // try {
        //     // 1. 解析文档
        //     DocumentParser.ParseResult parsed = documentParser.parse(filePath);
        //
        //     // 2. 切分
        //     List<ChunkSplitter.Chunk> chunks = chunkSplitter.split(parsed.text());
        //
        //     // 3. 向量化
        //     List<float[]> embeddings = new ArrayList<>();
        //     for (ChunkSplitter.Chunk chunk : chunks) {
        //         float[] embedding = llmService.embed(chunk.text());
        //         embeddings.add(embedding);
        //     }
        //
        //     // 4. 写入向量库
        //     vectorStore.insertChunks(documentId, chunks, embeddings);
        //
        //     // 5. 更新文档状态
        //     // ...
        //
        //     return documentId;
        // } catch (Exception e) {
        //     log.error("文档处理失败: {}", fileName, e);
        //     // 更新文档状态为 FAILED
        //     throw new RuntimeException("文档处理失败: " + fileName, e);
        // }
        throw new UnsupportedOperationException("TODO: 实现文档上传全流程");
    }

    /**
     * TODO: 知识库检索（两阶段）。
     *
     * <h3>完整流程：</h3>
     * <pre>
     * 1. llmService.embed(query) → 查询向量
     * 2. vectorStore.search(queryEmbedding, 20) → 粗排 top-20
     * 3. rerankService.rerank(query, candidates, topK) → 精排 top-K
     * 4. 返回精排后的结果
     * </pre>
     *
     * <h3>返回格式（用于拼接 System Prompt）：</h3>
     * <pre>{@code
     * 参考资料：
     * ---
     * [来源1] ...chunk_text...
     * ---
     * [来源2] ...chunk_text...
     * ---
     * }</pre>
     *
     * @param query 用户查询
     * @param topK  最终返回的文档片段数（建议 3）
     * @return 格式化后的参考资料文本
     */
    public String search(String query, int topK) {
        // TODO: 实现两阶段检索
        //
        // 1. 向量化查询
        // float[] queryEmbedding = llmService.embed(query);
        //
        // 2. 粗排 top-20
        // List<VectorStoreService.SearchResult> candidates = vectorStore.search(queryEmbedding, 20);
        //
        // 3. 精排 top-K
        // List<RerankService.RerankResult> reranked = rerankService.rerank(query, candidates, topK);
        //
        // 4. 格式化为参考资料文本
        // StringBuilder sb = new StringBuilder("参考资料：\n");
        // for (int i = 0; i < reranked.size(); i++) {
        //     sb.append("---\n");
        //     sb.append("[来源").append(i + 1).append("] ");
        //     sb.append(reranked.get(i).chunk().chunkText());
        //     sb.append("\n");
        // }
        // return sb.toString();
        throw new UnsupportedOperationException("TODO: 实现知识库检索");
    }
}

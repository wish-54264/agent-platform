package com.agent.rag;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PGVector 向量存储服务 — 管理知识库向量的写入和相似检索。
 * <p>
 * 基于 PostgreSQL + pgvector 扩展，使用 IVFFlat 索引做近似最近邻搜索。
 *
 * <h3>核心 SQL：</h3>
 * <pre>{@code
 * -- 写入向量
 * INSERT INTO knowledge_chunks (id, document_id, chunk_index, chunk_text, embedding)
 * VALUES (?, ?, ?, ?, ?::vector)
 *
 * -- 余弦相似度检索（<=> 是 pgvector 的余弦距离算子）
 * SELECT id, document_id, chunk_index, chunk_text,
 *        1 - (embedding <=> ?::vector) AS similarity
 * FROM knowledge_chunks
 * ORDER BY embedding <=> ?::vector
 * LIMIT ?
 * }</pre>
 *
 * <h3>你需要实现的内容：</h3>
 * <ol>
 *   <li>{@link #insertChunks(UUID, List, List)} — 批量写入向量</li>
 *   <li>{@link #search(float[], int)} — 向量相似检索</li>
 * </ol>
 */
@Slf4j
public class VectorStoreService {

    private final DataSource dataSource;

    public VectorStoreService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 检索结果。
     */
    public record SearchResult(
            UUID chunkId,
            UUID documentId,
            int chunkIndex,
            String chunkText,
            double similarity       // 余弦相似度 (0~1)，越大越相关
    ) {}

    /**
     * TODO: 批量插入向量块。
     *
     * <h3>实现提示：</h3>
     * 使用 JDBC batch insert 或 COPY 提高写入效率。
     * embedding 数组需要转为 pgvector 能识别的格式：
     * {@code [0.123, -0.456, ...]} → 字符串形式传给 {@code ?::vector}
     *
     * <pre>{@code
     * String sql = """
     *     INSERT INTO knowledge_chunks (id, document_id, chunk_index, chunk_text, embedding)
     *     VALUES (?, ?, ?, ?, ?::vector)
     *     """;
     *
     * try (Connection conn = dataSource.getConnection();
     *      PreparedStatement stmt = conn.prepareStatement(sql)) {
     *     for (int i = 0; i < chunks.size(); i++) {
     *         stmt.setObject(1, UUID.randomUUID());
     *         stmt.setObject(2, documentId);
     *         stmt.setInt(3, chunks.get(i).index());
     *         stmt.setString(4, chunks.get(i).text());
     *         stmt.setString(5, embeddingToString(embeddings.get(i)));
     *         stmt.addBatch();
     *     }
     *     stmt.executeBatch();
     * }
     * }</pre>
     *
     * @param documentId 文档 ID
     * @param chunks     切块列表
     * @param embeddings 每个块的向量（顺序对应）
     */
    public void insertChunks(UUID documentId,
                              List<ChunkSplitter.Chunk> chunks,
                              List<float[]> embeddings) {
        // TODO: 批量插入向量
        throw new UnsupportedOperationException("TODO: 批量插入向量");
    }

    /**
     * TODO: 向量相似检索（粗排阶段）。
     *
     * <h3>实现提示：</h3>
     * 使用 pgvector 的 {@code <=>} 余弦距离算子。
     *
     * <pre>{@code
     * String sql = """
     *     SELECT id, document_id, chunk_index, chunk_text,
     *            1 - (embedding <=> ?::vector) AS similarity
     *     FROM knowledge_chunks
     *     ORDER BY embedding <=> ?::vector
     *     LIMIT ?
     *     """;
     * }</pre>
     *
     * <h3>关于向量索引：</h3>
     * 数据量小时（< 几千条），全表扫描反而更快。
     * 数据量大后，创建 IVFFlat 索引：
     * <pre>{@code
     * CREATE INDEX ON knowledge_chunks
     *     USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
     * }</pre>
     *
     * @param queryEmbedding 查询文本的向量
     * @param topK           返回前 K 个最相似的结果
     * @return 相似检索结果列表（按相似度降序）
     */
    public List<SearchResult> search(float[] queryEmbedding, int topK) {
        // TODO: 实现向量相似检索
        throw new UnsupportedOperationException("TODO: 实现向量检索");
    }

    /**
     * TODO: 删除指定文档的所有向量块。
     *
     * <pre>{@code
     * DELETE FROM knowledge_chunks WHERE document_id = ?
     * }</pre>
     */
    public void deleteByDocumentId(UUID documentId) {
        // TODO: 删除文档向量
        throw new UnsupportedOperationException("TODO: 删除文档向量");
    }

    // ===== 辅助方法 =====

    /**
     * 将 float[] 转为 pgvector 兼容的字符串格式。
     * <p>
     * 例如: {@code [0.1, -0.2, 0.3]} → {@code "[0.1,-0.2,0.3]"}
     */
    static String embeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}

package com.agent.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 重排序服务 — 调用阿里云百炼 Rerank API 做精排。
 * <p>
 * 两阶段检索：
 * <ol>
 *   <li>粗排：PGVector 向量相似度 → top-20</li>
 *   <li>精排：百炼 Rerank（Cross-Encoder）→ top-3</li>
 * </ol>
 *
 * <h3>百炼 Rerank API：</h3>
 * <pre>{@code
 * POST https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank
 * {
 *   "model": "gte-rerank",
 *   "query": "用户问题",
 *   "documents": ["文档片段1", "文档片段2", ...],
 *   "top_n": 3
 * }
 * }</pre>
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #rerank(String, List, int)} — 调用百炼 Rerank API
 */
@Slf4j
public class RerankService {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String model;  // 默认: gte-rerank

    private static final String BASE_URL = "https://dashscope.aliyuncs.com";
    private static final String RERANK_PATH = "/api/v1/services/rerank/text-rerank/text-rerank";

    public RerankService(String apiKey, String model) {
        this.model = model != null ? model : "gte-rerank";
        this.mapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 重排序结果。
     */
    public record RerankResult(
            VectorStoreService.SearchResult chunk,
            double relevanceScore   // 相关性分数，越大越相关
    ) {}

    /**
     * TODO: 调用百炼 Rerank API 重排序。
     *
     * <h3>实现步骤：</h3>
     * <ol>
     *   <li>构建请求体：query + documents（文本数组）+ top_n</li>
     *   <li>POST 到百炼 Rerank API</li>
     *   <li>解析响应中的 results 数组（每个结果含 index 和 relevance_score）</li>
     *   <li>按 relevance_score 降序排列</li>
     *   <li>取 topN 个结果返回</li>
     * </ol>
     *
     * <h3>API 响应格式：</h3>
     * <pre>{@code
     * {
     *   "output": {
     *     "results": [
     *       {"index": 0, "document": {...}, "relevance_score": 0.95},
     *       {"index": 2, "document": {...}, "relevance_score": 0.72},
     *       ...
     *     ]
     *   },
     *   "usage": {"total_tokens": 150}
     * }
     * }</pre>
     *
     * @param query       用户查询文本
     * @param candidates  粗排结果（top-20）
     * @param topN        精排后返回的数量（如 3）
     * @return 重排序后的结果
     */
    public List<RerankResult> rerank(String query,
                                      List<VectorStoreService.SearchResult> candidates,
                                      int topN) {
        // TODO: 调用百炼 Rerank API
        //
        // 1. 构建请求体
        // 2. 发送 POST 请求
        // 3. 解析 results
        // 4. 按 relevance_score 排序
        // 5. 取 topN 返回
        throw new UnsupportedOperationException("TODO: 实现百炼 Rerank");
    }
}

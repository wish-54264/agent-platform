package com.agent.server.controller;

import com.agent.rag.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * 知识库 API 控制器。
 *
 * <h3>接口：</h3>
 * <ul>
 *   <li>{@code POST /api/knowledge/upload} — 上传文档</li>
 *   <li>{@code GET  /api/knowledge/search?q=...&topK=3} — 检索知识库</li>
 * </ul>
 *
 * <h3>你需要实现的内容：</h3>
 * 两个接口的实现逻辑（委托给 KnowledgeService）。
 */
@Slf4j
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * TODO: 文档上传接口。
     *
     * <p>接收 multipart/form-data 文件上传，保存到临时目录后
     * 提交给 KnowledgeService 做异步处理。
     *
     * <h3>实现要点：</h3>
     * <ul>
     *   <li>用 {@link FilePart#transferTo(Path)} 保存到临时文件</li>
     *   <li>上传成功后立即返回文档 ID（异步处理在后台进行）</li>
     *   <li>文件类型校验（白名单：pdf, docx, doc, txt, md）</li>
     *   <li>文件大小限制（如 50MB）</li>
     * </ul>
     *
     * <pre>{@code
     * @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     * public Mono<Map<String, Object>> upload(@RequestPart("file") FilePart filePart) {
     *     Path tempFile = Files.createTempFile("upload_", "_" + filePart.filename());
     *     return filePart.transferTo(tempFile)
     *         .then(Mono.fromCallable(() -> {
     *             UUID docId = knowledgeService.uploadDocument(tempFile, filePart.filename());
     *             return Map.of("documentId", docId, "status", "PROCESSING");
     *         }));
     * }
     * }</pre>
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Map<String, Object>> upload(@RequestPart("file") FilePart filePart) {
        // TODO: 实现文档上传接口
        throw new UnsupportedOperationException("TODO: 实现文档上传接口");
    }

    /**
     * TODO: 知识库检索接口（调试用）。
     *
     * <p>直接调用 RAG 检索，不经过 Agent 引擎。
     * 方便在开发阶段单独验证 RAG 管道的检索质量。
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam("q") String query,
                                       @RequestParam(defaultValue = "3") int topK) {
        // TODO: 实现知识库检索接口
        throw new UnsupportedOperationException("TODO: 实现知识库检索接口");
    }
}

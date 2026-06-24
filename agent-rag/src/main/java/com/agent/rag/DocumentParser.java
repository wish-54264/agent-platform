package com.agent.rag;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文档解析器 — 基于 Apache Tika 提取文档文本内容。
 * <p>
 * 支持格式：PDF、Word(doc/docx)、Markdown、TXT、HTML 等。
 * 对于 Tika 无法提取文本的 PDF（如扫描件/复印件），
 * 自动回退到 OCR 服务。
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #parse(Path)} — 解析文档并返回纯文本
 */
@Slf4j
public class DocumentParser {

    private final Tika tika;
    private final OcrService ocrService;

    /** 文本长度阈值：Tika 提取的文本少于此值时触发 OCR */
    private static final int MIN_TEXT_LENGTH_FOR_OCR = 100;

    public DocumentParser(OcrService ocrService) {
        this.tika = new Tika();
        this.ocrService = ocrService;
    }

    /**
     * 解析结果。
     */
    public record ParseResult(
            String text,
            String method,    // "tika" 或 "ocr"
            String mediaType  // 如 "application/pdf"
    ) {}

    /**
     * TODO: 解析文档文件，提取文本内容。
     *
     * <h3>实现步骤：</h3>
     * <ol>
     *   <li>用 Tika 检测文件 MIME 类型</li>
     *   <li>用 Tika 提取文本（{@code tika.parseToString(inputStream)}）</li>
     *   <li>如果提取的文本长度 < MIN_TEXT_LENGTH_FOR_100：
     *     <ul>
     *       <li>如果是 PDF → 调用 OCR 服务</li>
     *       <li>其他格式 → 返回空文本 + 警告日志</li>
     *     </ul>
     *   </li>
     *   <li>返回 ParseResult</li>
     * </ol>
     *
     * <h3>关键 API：</h3>
     * <pre>{@code
     * // 检测文件类型
     * String mediaType = tika.detect(filePath);
     *
     * // 提取文本
     * try (InputStream is = Files.newInputStream(filePath)) {
     *     String text = tika.parseToString(is);
     * }
     * }</pre>
     *
     * @param filePath 文档文件路径
     * @return 解析结果（纯文本 + 解析方式）
     */
    public ParseResult parse(Path filePath) throws IOException, TikaException {
        // TODO: 实现文档解析
        // 1. 检测 MIME 类型
        // 2. Tika 提取文本
        // 3. 判断是否需要 OCR
        // 4. 返回 ParseResult
        throw new UnsupportedOperationException("TODO: 实现文档解析");
    }
}

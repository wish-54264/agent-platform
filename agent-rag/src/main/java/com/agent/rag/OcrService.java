package com.agent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
/**
 * 阿里云百炼 OCR 服务 — 处理 PDF 扫描件/复印件。
 * <p>
 * 当 Apache Tika 无法从 PDF 中提取足够文字时（扫描件），
 * 调用百炼 OCR API 进行文字识别。
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #recognize(Path)} — 调用百炼 OCR API 识别图片文字
 *
 * <h3>百炼 OCR API 文档：</h3>
 * <pre>{@code
 * POST https://dashscope.aliyuncs.com/api/v1/services/aigc/image2text/image2text
 * }</pre>
 * 参考：https://help.aliyun.com/zh/model-studio/ocr
 */
@Slf4j
public class OcrService {

    private final WebClient webClient;
    private final String apiKey;


    private static final String BASE_URL = "https://dashscope.aliyuncs.com";
    private static final String OCR_PATH = "/api/v1/services/aigc/image2text/image2text";

    public OcrService(String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * TODO: 调用百炼 OCR 识别 PDF 扫描件中的文字。
     *
     * <h3>实现步骤：</h3>
     * <ol>
     *   <li>将 PDF 文件转为 Base64 编码（或按页转为图片）</li>
     *   <li>构造 OCR API 请求（参考百炼文档）</li>
     *   <li>发送请求并解析返回的文字内容</li>
     *   <li>将识别结果拼接为完整的纯文本</li>
     * </ol>
     *
     * <h3>实现提示：</h3>
     * 百炼 OCR 支持直接传 PDF 文件（base64），也支持传图片。
     * PDF 扫描件建议先按页截图再逐页识别，质量更高。
     *
     * <pre>{@code
     * // 伪代码
     * byte[] fileBytes = Files.readAllBytes(filePath);
     * String base64 = Base64.getEncoder().encodeToString(fileBytes);
     *
     * ObjectNode request = mapper.createObjectNode();
     * request.put("model", "ocr-v1");
     * // ... 按百炼 API 文档构建请求
     *
     * String response = webClient.post()
     *     .uri(OCR_PATH)
     *     .bodyValue(request)
     *     .retrieve()
     *     .bodyToMono(String.class)
     *     .block();
     *
     * return parseOcrResponse(response);
     * }</pre>
     *
     * @param filePath PDF 扫描件路径
     * @return 识别出的文本内容
     */
    public String recognize(Path filePath) {
       try {
        // 1. 读取文件转 Base64
        byte[] fileBytes = Files.readAllBytes(filePath);
        String base64 = java.util.Base64.getEncoder().encodeToString(fileBytes);

        // 2. 构建请求
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.put("model", "qwen-vl-ocr");
        ObjectNode input = body.putObject("input");
        ArrayNode messages = input.putArray("messages");
        ObjectNode msg = messages.addObject();
        msg.put("role", "user");
        ArrayNode content = msg.putArray("content");
        ObjectNode imgPart = content.addObject();
        imgPart.put("image", "data:application/pdf;base64," + base64);

        // 3. POST 到百炼
        String responseJson = webClient.post()
                .uri(OCR_PATH)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 4. 解析返回的文字
        JsonNode root = new ObjectMapper().readTree(responseJson);
        return root.path("output").path("choices").get(0)
                .path("message").path("content").asText();

    } catch (Exception e) {
        log.error("OCR 识别失败", e);
        throw new RuntimeException("百炼 OCR 失败: " + e.getMessage(), e);
    }
}
}

package com.agent.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSON Schema 校验器 — 对 LLM 返回的工具调用参数做第二道校验。
 * <p>
 * 虽然 LLM 的 FC 模式下参数已经是结构化 JSON，
 * 但 LLM 仍可能犯以下错误：
 * <ul>
 *   <li>缺少必填参数</li>
 *   <li>参数类型错误（该传 string 传了 number）</li>
 *   <li>参数值不在枚举范围内</li>
 *   <li>参数格式不符合约束（如字符串长度超限）</li>
 * </ul>
 * <p>
 * 此校验器不是完整的 JSON Schema 实现，而是覆盖最常见校验场景的轻量版本。
 *
 * <h3>你需要实现的内容：</h3>
 * {@link #validate(JsonNode, JsonNode)} — 核心校验逻辑
 */
@Slf4j
public class SchemaValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 校验结果。
     */
    public record ValidationResult(boolean valid, List<String> errors) {
        public static ValidationResult success() {
            return new ValidationResult(true, List.of());
        }
        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }
    }

    /**
     * TODO: 根据 JSON Schema 校验参数。
     *
     * <h3>需要支持的校验规则（按优先级）：</h3>
     * <ol>
     *   <li><b>required</b> — 必填字段检查</li>
     *   <li><b>type</b> — 类型检查（string / integer / number / boolean / array / object）</li>
     *   <li><b>properties</b> — 嵌套对象属性校验（递归）</li>
     *   <li><b>enum</b> — 枚举值检查</li>
     * </ol>
     *
     * <h3>实现提示：</h3>
     * <pre>{@code
     * if (schema.has("required")) {
     *     for (JsonNode requiredField : schema.get("required")) {
     *         if (!arguments.has(requiredField.asText())) {
     *             errors.add("缺少必填参数: " + requiredField.asText());
     *         }
     *     }
     * }
     *
     * if (schema.has("properties") && arguments != null) {
     *     JsonNode properties = schema.get("properties");
     *     Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
     *     while (fields.hasNext()) {
     *         Map.Entry<String, JsonNode> field = fields.next();
     *         String fieldName = field.getKey();
     *         JsonNode fieldSchema = field.getValue();
     *         if (arguments.has(fieldName)) {
     *             validateType(fieldName, arguments.get(fieldName), fieldSchema, errors);
     *         }
     *     }
     * }
     * }</pre>
     *
     * @param schema    JSON Schema 定义
     * @param arguments LLM 返回的实际参数
     * @return 校验结果
     */
    public ValidationResult validate(JsonNode schema, JsonNode arguments) {
        // TODO: 实现 JSON Schema 校验
        List<String> errors = new ArrayList<>();

        if (schema == null || !schema.has("type")) {
            return ValidationResult.success(); // 无 Schema 则不校验
        }

        // 1. 检查 required 字段
        // 2. 检查 properties 中每个字段的类型
        // 3. 检查 enum 约束
        if(schema.has("required")){
            for(JsonNode requiredField : schema.get("required")){
                if(!arguments.has(requiredField.asText())){
                    errors.add("缺少必要参数："+requiredField.asText());
                }

            }
        }
        if(schema.has("properties")&& arguments != null){
            JsonNode properties = schema.get("properties");
            var fields = properties.fields();
            while(fields.hasNext()){
                var field = fields.next();
                String fieldName = field.getKey();
                JsonNode fieldSchema = field.getValue();
                if(arguments.has(fieldName)){
                    String expectedType = fieldSchema.has("type")
                                        ? fieldSchema.get("type").asText():null;
                    JsonNode value = arguments.get(fieldName);
                    if("integer".equals(expectedType)&&!value.isInt()){
                        errors.add(fieldName+"应该为整数");
                    }
                    if("string".equals(expectedType)&&!value.isTextual()){
                        errors.add(fieldName + "应为字符串");
                    }
                    if("number".equals(expectedType)&&!value.isNumber()){
                        errors.add(fieldName +"应为数字");
                    }
                    if ("boolean".equals(expectedType) && !value.isBoolean()) {
                        errors.add(fieldName + " 应为布尔值");
            }
                }
            }
        }
        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }
}

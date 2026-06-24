package com.agent.mcp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON Schema 构建工具 — 流式 API 构建工具参数 Schema。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * SchemaFactory.object()
 *     .add("city", SchemaFactory.string("城市名称，如'北京'"))
 *     .add("days", SchemaFactory.integer("预报天数，1-7"))
 *     .required("city")
 *     .build();
 * }</pre>
 *
 * <h3>这将生成：</h3>
 * <pre>{@code
 * {
 *   "type": "object",
 *   "properties": {
 *     "city": {"type": "string", "description": "城市名称，如'北京'"},
 *     "days": {"type": "integer", "description": "预报天数，1-7"}
 *   },
 *   "required": ["city"]
 * }
 * }</pre>
 */
public class SchemaFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 创建一个 object 类型的 Schema 构建器。
     */
    public static ObjectSchemaBuilder object() {
        return new ObjectSchemaBuilder();
    }

    /**
     * 创建一个 string 类型的属性 Schema。
     */
    public static ObjectNode string(String description) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "string");
        node.put("description", description);
        return node;
    }

    /**
     * 创建一个 integer 类型的属性 Schema。
     */
    public static ObjectNode integer(String description) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "integer");
        node.put("description", description);
        return node;
    }

    /**
     * 创建一个 number 类型的属性 Schema。
     */
    public static ObjectNode number(String description) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "number");
        node.put("description", description);
        return node;
    }

    /**
     * 创建一个 boolean 类型的属性 Schema。
     */
    public static ObjectNode bool(String description) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "boolean");
        node.put("description", description);
        return node;
    }

    /**
     * 创建一个 array 类型的属性 Schema。
     */
    public static ObjectNode array(String description, ObjectNode items) {
        ObjectNode node = mapper.createObjectNode();
        node.put("type", "array");
        node.put("description", description);
        node.set("items", items);
        return node;
    }

    /**
     * Object Schema 流式构建器。
     */
    public static class ObjectSchemaBuilder {
        private final ObjectNode properties = mapper.createObjectNode();
        private final ArrayNode required = mapper.createArrayNode();

        /**
         * 添加一个属性。
         */
        public ObjectSchemaBuilder add(String name, ObjectNode schema) {
            properties.set(name, schema);
            return this;
        }

        /**
         * 标记某个属性为必填。
         */
        public ObjectSchemaBuilder required(String name) {
            required.add(name);
            return this;
        }

        /**
         * 构建最终的 JSON Schema。
         */
        public ObjectNode build() {
            ObjectNode schema = mapper.createObjectNode();
            schema.put("type", "object");
            schema.set("properties", properties);
            if (!required.isEmpty()) {
                schema.set("required", required);
            }
            return schema;
        }
    }
}

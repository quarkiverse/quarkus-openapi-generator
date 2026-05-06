/*
 * Copyright 2021 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apicurio.hub.api.codegen.pre;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.apicurio.datamodels.models.Schema;
import io.apicurio.datamodels.models.openapi.v31.OpenApi31Schema;
import io.apicurio.datamodels.util.NodeUtil;
import io.apicurio.hub.api.codegen.CodegenExtensions;
import io.apicurio.hub.api.codegen.jaxrs.TraversingOpenApi31VisitorAdapter;
import io.apicurio.hub.api.codegen.util.CodegenUtil;

/**
 * Extends the built-in map processor so the server generator can inline array-valued maps
 * as {@code Map<String, List<...>>}.
 */
public class OpenApiMapDataTypeProcessor extends TraversingOpenApi31VisitorAdapter {

    private static final Map<String, String> EXTENSION_NAMES = createExtensionNames();

    @Override
    public void visitSchema(Schema node) {
        if (!NodeUtil.isDefinition(node)) {
            return;
        }

        OpenApi31Schema schema = (OpenApi31Schema) node;
        if (!isMapType(schema)) {
            return;
        }

        OpenApi31Schema additionalProperties = schema.getAdditionalProperties() != null
                && schema.getAdditionalProperties().isSchema()
                        ? (OpenApi31Schema) schema.getAdditionalProperties().asSchema()
                        : null;
        schema.setAdditionalProperties(null);
        String mapType = buildJavaType(schema, additionalProperties);
        schema.addExtraProperty("existingJavaType", factory.textNode(mapType));
    }

    private boolean isMapType(OpenApi31Schema schema) {
        JsonNode extension = CodegenUtil.getExtension(schema, CodegenExtensions.TYPE);
        if (extension == null || !extension.isTextual()) {
            return false;
        }
        return EXTENSION_NAMES.containsKey(extension.asText());
    }

    private static Map<String, String> createExtensionNames() {
        Map<String, String> names = new HashMap<>();
        names.put("StringMap", "java.util.Map<String,String>");
        names.put("StringObjectMap", "java.util.Map<String,Object>");
        names.put("ArrayMap", null);
        return names;
    }

    private String buildJavaType(OpenApi31Schema schema, OpenApi31Schema additionalProperties) {
        String alias = CodegenUtil.getExtension(schema, CodegenExtensions.TYPE).asText();
        if (!"ArrayMap".equals(alias)) {
            return EXTENSION_NAMES.get(alias);
        }

        String valueType = "java.lang.Object";
        if (additionalProperties != null) {
            valueType = resolveJavaType(additionalProperties);
        }
        return "java.util.Map<String,java.util.List<" + valueType + ">>";
    }

    private String resolveJavaType(OpenApi31Schema schema) {
        if (schema == null) {
            return "java.lang.Object";
        }

        if (schema.get$ref() != null && !schema.get$ref().isBlank()) {
            return refToSimpleType(schema.get$ref());
        }

        if (schema.getType() != null) {
            if (CodegenUtil.containsValue(schema.getType(), "array")) {
                String itemType = "java.lang.Object";
                if (schema.getItems() != null) {
                    itemType = resolveJavaType((OpenApi31Schema) schema.getItems());
                }
                return "java.util.List<" + itemType + ">";
            }
            if (CodegenUtil.containsValue(schema.getType(), "string")) {
                return "java.lang.String";
            }
            if (CodegenUtil.containsValue(schema.getType(), "integer")) {
                return "java.lang.Integer";
            }
            if (CodegenUtil.containsValue(schema.getType(), "number")) {
                return "java.lang.Double";
            }
            if (CodegenUtil.containsValue(schema.getType(), "boolean")) {
                return "java.lang.Boolean";
            }
        }

        return "java.lang.Object";
    }

    private String refToSimpleType(String ref) {
        int lastSlash = ref.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < ref.length() - 1) {
            return ref.substring(lastSlash + 1);
        }
        int lastHash = ref.lastIndexOf('#');
        if (lastHash >= 0 && lastHash < ref.length() - 1) {
            return ref.substring(lastHash + 1);
        }
        return ref;
    }
}

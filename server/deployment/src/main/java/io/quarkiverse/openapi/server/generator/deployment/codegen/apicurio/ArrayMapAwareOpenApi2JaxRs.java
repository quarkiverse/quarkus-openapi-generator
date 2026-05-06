package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.apicurio.datamodels.Library;
import io.apicurio.datamodels.TraverserDirection;
import io.apicurio.datamodels.models.Document;
import io.apicurio.datamodels.models.ModelType;
import io.apicurio.hub.api.codegen.OpenApi2JaxRs;
import io.apicurio.hub.api.codegen.beans.CodegenInfo;
import io.apicurio.hub.api.codegen.beans.CodegenJavaBean;
import io.apicurio.hub.api.codegen.beans.CodegenJavaInterface;
import io.apicurio.hub.api.codegen.jaxrs.CodegenTarget;
import io.apicurio.hub.api.codegen.jaxrs.InterfacesVisitor;
import io.apicurio.hub.api.codegen.jaxrs.OpenApi2CodegenVisitor;

/**
 * Adds support for ArrayMap schemas so request bodies can be generated as
 * {@code Map<String, List<T>>} instead of falling back to {@code InputStream}.
 */
public class ArrayMapAwareOpenApi2JaxRs extends OpenApi2JaxRs {

    private static final Set<String> HTTP_METHOD_KEYS = Set.of("get", "put", "post", "delete", "options", "head",
            "patch", "trace");

    private Map<String, String> requestBodyTypesByOperationId = Map.of();

    @Override
    protected CodegenInfo getInfoFromApiDoc() throws IOException {
        document = Library.readDocumentFromJSONString(openApiDoc);
        document = Library.transformDocument(document, ModelType.OPENAPI31);
        requestBodyTypesByOperationId = computeRequestBodyTypes(document);
        document = preProcess(document);

        InterfacesVisitor iVisitor = new InterfacesVisitor();
        Library.visitTree(document, iVisitor, TraverserDirection.down);

        OpenApi2CodegenVisitor cgVisitor = new OpenApi2CodegenVisitor(this.settings, iVisitor.getInterfaces(),
                CodegenTarget.JAX_RS);
        Library.visitTree(document, cgVisitor, TraverserDirection.down);

        CodegenInfo info = cgVisitor.getCodegenInfo();
        info.getInterfaces().forEach(iface -> {
            iface.getMethods().forEach(method -> {
                method.getArguments().forEach(arg -> {
                    String argTypeSig = arg.getTypeSignature();
                    CodegenJavaBean matchingBean = findMatchingBean(info, argTypeSig);
                    if (matchingBean != null) {
                        arg.setType(matchingBean.getPackage() + "." + StringUtils.capitalize(matchingBean.getName()));
                    }
                });
            });
        });
        String contextRoot = getContextRoot(document);
        if (contextRoot != null) {
            info.setContextRoot(contextRoot);
        }
        return info;
    }

    @Override
    protected String generateJavaInterface(CodegenInfo info, CodegenJavaInterface interfaceInfo, String topLevelPackage) {
        String source = super.generateJavaInterface(info, interfaceInfo, topLevelPackage);
        for (var method : interfaceInfo.getMethods()) {
            String replacementType = requestBodyTypesByOperationId.get(method.getOperationId());
            if (replacementType != null) {
                source = replaceRequestBodyType(source, method.getOperationId(), method.getName(), replacementType);
            }
        }
        return source;
    }

    private String replaceRequestBodyType(String source, String operationId, String methodName, String replacementType) {
        String operationMarker = "operationId = \"" + operationId + "\"";
        int opIndex = source.indexOf(operationMarker);
        if (opIndex < 0) {
            return source;
        }

        int methodIndex = source.indexOf("void " + methodName + "(", opIndex);
        if (methodIndex < 0) {
            return source;
        }

        int methodEnd = source.indexOf(";", methodIndex);
        if (methodEnd < 0) {
            return source;
        }

        String methodSignature = source.substring(methodIndex, methodEnd);
        String rewrittenSignature = methodSignature.replaceFirst("\\bInputStream\\b", replacementType);
        if (methodSignature.equals(rewrittenSignature)) {
            return source;
        }
        return source.substring(0, methodIndex) + rewrittenSignature + source.substring(methodEnd);
    }

    private Map<String, String> computeRequestBodyTypes(Document document) throws IOException {
        JsonNode json = mapper.readTree(Library.writeDocumentToJSONString(document));
        Map<String, String> arrayMapTypes = computeArrayMapTypes(json);
        Map<String, String> requestBodyTypes = new HashMap<>();

        JsonNode paths = json.path("paths");
        if (!paths.isObject()) {
            return requestBodyTypes;
        }

        paths.fields().forEachRemaining(pathEntry -> {
            JsonNode pathItem = pathEntry.getValue();
            pathItem.fields().forEachRemaining(methodEntry -> {
                if (!HTTP_METHOD_KEYS.contains(methodEntry.getKey())) {
                    return;
                }

                JsonNode operation = methodEntry.getValue();
                String operationId = textValue(operation, "operationId");
                if (operationId == null) {
                    return;
                }

                JsonNode requestBody = operation.path("requestBody");
                String requestType = resolveBodyType(requestBody, arrayMapTypes);
                if (requestType != null) {
                    requestBodyTypes.put(operationId, requestType);
                }
            });
        });

        return requestBodyTypes;
    }

    private Map<String, String> computeArrayMapTypes(JsonNode json) {
        Map<String, String> arrayMapTypes = new HashMap<>();
        JsonNode schemas = json.path("components").path("schemas");
        if (!schemas.isObject()) {
            return arrayMapTypes;
        }

        schemas.fields().forEachRemaining(entry -> {
            JsonNode schema = entry.getValue();
            JsonNode typeNode = schema.path("x-codegen-type");
            if (!typeNode.isTextual() || !"ArrayMap".equals(typeNode.asText())) {
                return;
            }

            JsonNode additionalProperties = schema.path("additionalProperties");
            if (!additionalProperties.isObject()) {
                return;
            }

            String valueType = resolveSchemaType(additionalProperties, arrayMapTypes);
            arrayMapTypes.put(entry.getKey(), "java.util.Map<String, java.util.List<" + valueType + ">>");
        });

        return arrayMapTypes;
    }

    private String resolveBodyType(JsonNode requestBody, Map<String, String> arrayMapTypes) {
        JsonNode content = requestBody.path("content");
        if (!content.isObject() || content.size() == 0) {
            return null;
        }

        JsonNode mediaType = content.elements().next();
        JsonNode schema = mediaType.path("schema");
        if (!schema.isObject()) {
            return null;
        }

        return resolveSchemaType(schema, arrayMapTypes);
    }

    private String resolveSchemaType(JsonNode schema, Map<String, String> arrayMapTypes) {
        JsonNode ref = schema.get("$ref");
        if (ref != null && ref.isTextual()) {
            String refName = ref.asText();
            String arrayMapType = arrayMapTypes.get(refName.substring(refName.lastIndexOf('/') + 1));
            if (arrayMapType != null) {
                return arrayMapType;
            }
            return refName.substring(refName.lastIndexOf('/') + 1);
        }

        JsonNode type = schema.get("type");
        if (type != null && type.isTextual()) {
            switch (type.asText()) {
                case "array":
                    JsonNode items = schema.get("items");
                    String itemType = items != null ? resolveSchemaType(items, arrayMapTypes) : "java.lang.Object";
                    return "java.util.List<" + itemType + ">";
                case "string":
                    return "java.lang.String";
                case "integer":
                    return "java.lang.Integer";
                case "number":
                    return "java.lang.Double";
                case "boolean":
                    return "java.lang.Boolean";
                case "object":
                    JsonNode additionalProperties = schema.get("additionalProperties");
                    if (additionalProperties != null && additionalProperties.isObject()) {
                        String valueType = resolveSchemaType(additionalProperties, arrayMapTypes);
                        return "java.util.Map<String, " + valueType + ">";
                    }
                    return "java.lang.Object";
                default:
                    return "java.lang.Object";
            }
        }

        return "java.lang.Object";
    }

    private String textValue(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && value.isTextual() ? value.asText() : null;
    }
}

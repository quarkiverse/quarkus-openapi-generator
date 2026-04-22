package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.qute;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.openapitools.codegen.CodegenSecurity;
import org.openapitools.codegen.model.OperationMap;

import io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.OpenApiGeneratorOutputPaths;
import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Expression;
import io.quarkus.qute.NamespaceResolver;

/**
 * Collection of OpenAPI specific methods to use in the templates.
 * It can be enhanced to have even more methods. See the usage of the method #genDeprecatedModelAttr to understand how to
 * implement and use them.
 */
public class OpenApiNamespaceResolver implements NamespaceResolver {
    static final OpenApiNamespaceResolver INSTANCE = new OpenApiNamespaceResolver();
    private static final String GENERATE_DEPRECATED_PROP = "generateDeprecated";
    private static final String EXT_PROFILE_PREFIX = "x-smallrye-profile-";
    private static final int EXT_PROFILE_PREFIX_LENGTH = EXT_PROFILE_PREFIX.length();
    private static final String VOID_TYPE = "void";
    private static final String RESPONSE_TYPE = "jakarta.ws.rs.core.Response";
    private static final String REST_RESPONSE_TYPE = "org.jboss.resteasy.reactive.RestResponse";
    private static final String MUTINY_UNI_TYPE = "io.smallrye.mutiny.Uni";
    private static final String CODEGEN_USE_REST_RESPONSE = "x-codegen-use-rest-response";
    private static final String CODEGEN_RETURN_TYPE = "x-codegen-returnType";

    private OpenApiNamespaceResolver() {
    }

    /**
     * @param pkg name of the given package
     * @param classname name of the Model class
     * @param codegenConfig Map with the model codegen properties
     * @return true if the given model class should generate the deprecated attributes
     */
    @SuppressWarnings("unused")
    public boolean genDeprecatedModelAttr(final String pkg, final String classname,
            final HashMap<String, Object> codegenConfig) {
        final String key = String.format("%s.%s.%s", pkg, classname, GENERATE_DEPRECATED_PROP);
        return Boolean.parseBoolean(codegenConfig.getOrDefault(key, "true").toString());
    }

    /**
     * @param pkg name of the given package
     * @param classname name of the Model class
     * @param codegenConfig Map with the model codegen properties
     * @return true if the given model class should generate the deprecated attributes
     */
    @SuppressWarnings("unused")
    public boolean genDeprecatedApiAttr(final String pkg, final String classname,
            final HashMap<String, Object> codegenConfig) {
        final String key = String.format("%s.%s.%s", pkg, classname, GENERATE_DEPRECATED_PROP);
        return Boolean.parseBoolean(codegenConfig.getOrDefault(key, "true").toString());
    }

    @SuppressWarnings("unused")
    public String parseUri(String uri) {
        return escapeWindowsPath(OpenApiGeneratorOutputPaths.getRelativePath(Path.of(URI.create(uri))).toString());
    }

    @SuppressWarnings("unused")
    public boolean hasAuthMethods(OperationMap operations) {
        return operations != null && operations.getOperation().stream().anyMatch(operation -> operation.hasAuthMethods);
    }

    /**
     * Ignore the OAuth flows by filtering every oauth instance by name. The inner openapi-generator library duplicates the
     * OAuth instances per flow in the openapi spec.
     * So a specification file with more than one flow defined has two entries in the list. For now, we do not use this
     * information in runtime so it can be safely filtered and ignored.
     *
     * @param oauthOperations passed through the Qute template
     * @see "resources/templates/libraries/microprofile/auth/compositeAuthenticationProvider.qute"
     * @return The list filtered by unique auth name
     */
    @SuppressWarnings("unused")
    public List<CodegenSecurity> getUniqueOAuthOperations(List<CodegenSecurity> oauthOperations) {
        if (oauthOperations != null) {
            return new ArrayList<>(oauthOperations.stream()
                    .collect(Collectors.toMap(security -> security.name, security -> security,
                            (existing, replacement) -> existing, LinkedHashMap::new))
                    .values());
        }
        return List.of();
    }

    /**
     * Returns the list of SmallRye profile names declared on an operation through vendor extensions.
     *
     * @param vendorExtensions vendor extensions attached to a codegen operation
     * @return the profile names, sorted for deterministic output
     */
    @SuppressWarnings("unused")
    public List<String> smallryeProfileExtensions(Map<String, Object> vendorExtensions) {
        if (vendorExtensions == null || vendorExtensions.isEmpty()) {
            return List.of();
        }

        return vendorExtensions.keySet().stream()
                .filter(key -> key.startsWith(EXT_PROFILE_PREFIX))
                .map(key -> key.substring(EXT_PROFILE_PREFIX_LENGTH))
                .sorted()
                .toList();
    }

    /**
     * Computes the final Java return type for an operation based on the operation return type,
     * reactive/rest-response flags and vendor extensions.
     */
    @SuppressWarnings("unused")
    public String getMapReturnType(final String opReturnType, final Boolean useReactiveConfig,
            final Boolean useRestResponseConfig, final Map<String, Object> codegenConfig) {
        final Map<String, Object> safeCodegenConfig = codegenConfig == null ? Map.of() : codegenConfig;
        final boolean useReactive = Boolean.TRUE.equals(useReactiveConfig);
        final String returnResponseTypeConfig = stringValue(safeCodegenConfig.get(CODEGEN_RETURN_TYPE));
        final boolean useRestResponse = Boolean.TRUE.equals(useRestResponseConfig)
                || isTrue(safeCodegenConfig.get(CODEGEN_USE_REST_RESPONSE))
                || REST_RESPONSE_TYPE.equals(returnResponseTypeConfig);
        final boolean hasReturnType = hasReturnType(opReturnType);
        final String responsePayloadType = hasReturnType ? opReturnType : "Void";

        if (useReactive) {
            if (useRestResponse) {
                return MUTINY_UNI_TYPE + "<" + REST_RESPONSE_TYPE + "<" + responsePayloadType + ">>";
            }
            if (RESPONSE_TYPE.equals(returnResponseTypeConfig)) {
                return MUTINY_UNI_TYPE + "<" + RESPONSE_TYPE + ">";
            }
            if (!returnResponseTypeConfig.isBlank()) {
                return MUTINY_UNI_TYPE + "<" + returnResponseTypeConfig + ">";
            }
            return hasReturnType ? MUTINY_UNI_TYPE + "<" + opReturnType + ">"
                    : MUTINY_UNI_TYPE + "<" + RESPONSE_TYPE
                            + ">";
        }

        if (useRestResponse) {
            return REST_RESPONSE_TYPE + "<" + responsePayloadType + ">";
        }
        if (RESPONSE_TYPE.equals(returnResponseTypeConfig)) {
            return RESPONSE_TYPE;
        }
        if (!returnResponseTypeConfig.isBlank()) {
            return returnResponseTypeConfig;
        }
        return hasReturnType ? opReturnType : RESPONSE_TYPE;
    }

    @SuppressWarnings("unused")
    public boolean hasSmallryeProfileExtensions(Map<String, Object> vendorExtensions) {
        return !smallryeProfileExtensions(vendorExtensions).isEmpty();
    }

    /**
     * Returns true when any operation resolves to a RestResponse return type.
     *
     * The generated API template uses this to add the import even when the raw
     * return type comes from a vendor extension such as x-codegen-returnType.
     */
    @SuppressWarnings("unused")
    public boolean hasRestResponseReturnType(OperationMap operations, final Boolean useReactiveConfig,
            final Boolean useRestResponseConfig) {
        if (operations == null || operations.getOperation() == null) {
            return false;
        }

        return operations.getOperation().stream()
                .map(op -> getMapReturnType(op.returnType, useReactiveConfig, useRestResponseConfig, op.vendorExtensions))
                .anyMatch(returnType -> returnType != null && returnType.contains("RestResponse"));
    }

    @Override
    public CompletionStage<Object> resolve(EvalContext context) {
        try {
            Object[] args = new Object[context.getParams().size()];
            Class<?>[] classArgs = new Class[context.getParams().size()];

            int i = 0;
            for (Expression expr : context.getParams()) {
                args[i] = context.evaluate(expr).toCompletableFuture().get();
                classArgs[i] = args[i].getClass();
                i++;
            }

            Method targetMethod = findCompatibleMethod(context.getName(), classArgs);
            if (targetMethod == null) {
                throw new NoSuchMethodException("No compatible method found for: " + context.getName());
            }

            return CompletableFuture.completedFuture(targetMethod.invoke(this, args));
        } catch (ReflectiveOperationException | InterruptedException | ExecutionException ex) {
            return CompletableFuture.failedStage(ex);
        }
    }

    private Method findCompatibleMethod(String methodName, Class<?>[] argTypes) {
        for (Method method : this.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (isAssignable(paramTypes, argTypes)) {
                    return method;
                }
            }
        }
        return null;
    }

    private boolean isAssignable(Class<?>[] paramTypes, Class<?>[] argTypes) {
        if (paramTypes.length != argTypes.length) {
            return false;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (!paramTypes[i].isAssignableFrom(argTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean hasReturnType(String opReturnType) {
        return opReturnType != null && !opReturnType.isBlank() && !VOID_TYPE.equals(opReturnType);
    }

    private boolean isTrue(Object value) {
        return Boolean.TRUE.equals(value) || Boolean.parseBoolean(String.valueOf(value));
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String escapeWindowsPath(String pathAsString) {
        return pathAsString.replace("\\", "\\\\"); // without it would lead into compile error in generated sources
    }

    @Override
    public String getNamespace() {
        return "openapi";
    }
}

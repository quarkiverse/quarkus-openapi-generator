package io.quarkiverse.openapi.generator.deployment.template;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.openapitools.codegen.CodegenSecurity;
import org.openapitools.codegen.model.OperationMap;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorOutputPaths;
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

    private OpenApiNamespaceResolver() {
    }

    /**
     * @param pkg name of the given package
     * @param classname name of the Model class
     * @param codegenConfig Map with the model codegen properties
     * @return true if the given model class should generate the deprecated attributes
     */
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
    public boolean genDeprecatedApiAttr(final String pkg, final String classname,
            final HashMap<String, Object> codegenConfig) {
        final String key = String.format("%s.%s.%s", pkg, classname, GENERATE_DEPRECATED_PROP);
        return Boolean.parseBoolean(codegenConfig.getOrDefault(key, "true").toString());
    }

    public String parseUri(String uri) {
        return OpenApiGeneratorOutputPaths.getRelativePath(Path.of(uri)).toString().replace(File.separatorChar, '/');
    }

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
    public List<CodegenSecurity> getUniqueOAuthOperations(List<CodegenSecurity> oauthOperations) {
        if (oauthOperations != null) {
            return new ArrayList<>(oauthOperations.stream()
                    .collect(Collectors.toMap(security -> security.name, security -> security,
                            (existing, replacement) -> existing, LinkedHashMap::new))
                    .values());
        }
        return Collections.emptyList();
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

    @Override
    public String getNamespace() {
        return "openapi";
    }
}

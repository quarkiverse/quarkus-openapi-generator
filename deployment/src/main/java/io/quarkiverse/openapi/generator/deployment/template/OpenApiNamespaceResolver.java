package io.quarkiverse.openapi.generator.deployment.template;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

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
    private static final String GENERATE_DEPRECATED_PROP = "generateDeprecated";

    static final OpenApiNamespaceResolver INSTANCE = new OpenApiNamespaceResolver();

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
        return OpenApiGeneratorOutputPaths.getRelativePath(Path.of(uri)).toString();
    }

    @Override
    public CompletionStage<Object> resolve(EvalContext context) {
        try {
            Class<?>[] classArgs = new Class[context.getParams().size()];
            Object[] args = new Object[context.getParams().size()];
            int i = 0;
            for (Expression expr : context.getParams()) {
                args[i] = context.evaluate(expr).toCompletableFuture().get();
                classArgs[i] = args[i].getClass();
                i++;
            }
            return CompletableFuture
                    .completedFuture(this.getClass().getMethod(context.getName(), classArgs).invoke(this, args));
        } catch (ReflectiveOperationException | InterruptedException | ExecutionException ex) {
            return CompletableFuture.failedStage(ex);
        }
    }

    @Override
    public String getNamespace() {
        return "openapi";
    }
}

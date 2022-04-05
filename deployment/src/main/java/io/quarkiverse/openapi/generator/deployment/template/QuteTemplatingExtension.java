package io.quarkiverse.openapi.generator.deployment.template;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Expression;
import io.quarkus.qute.NamespaceResolver;

public class QuteTemplatingExtension implements NamespaceResolver {

    static final QuteTemplatingExtension INSTANCE = new QuteTemplatingExtension();

    private QuteTemplatingExtension() {
    }

    public String parseUri(String uri) {
        return Path.of(uri).getFileName().toString();
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

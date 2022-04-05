package io.quarkiverse.openapi.generator.deployment.template;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.quarkus.qute.EvalContext;
import io.quarkus.qute.ValueResolver;

class ModelConfigValueResolver implements ValueResolver {

    private static final String M_GENERATE_MODEL_DEPRECATED = "generateModelDeprecated";
    private static final String GENERATE_DEPRECATED_PROP = "generateDeprecated";
    private static final String PACKAGE_PARAM = "package";
    private static final String CLASSNAME_PARAM = "m.classname";
    private static final String CODEGEN_CONFIG_PARAM = "codegen";

    @Override
    public CompletionStage<Object> resolve(EvalContext context) {
        final CompletionStage<Object> pkg = context.evaluate(PACKAGE_PARAM);
        final CompletionStage<Object> classname = context.evaluate(CLASSNAME_PARAM);
        final CompletionStage<Object> config = context.evaluate(CODEGEN_CONFIG_PARAM);

        // in the future, depending on the name of the method, we delegate the last composition to it
        return pkg.thenCompose(p -> classname
                .thenCompose(clazz -> config.thenCompose(cfg -> CompletableFuture.supplyAsync(() -> {
                    final String key = String.format("%s.%s.%s", p, clazz, GENERATE_DEPRECATED_PROP);
                    return Boolean.parseBoolean(((Map<String, Object>) cfg).getOrDefault(key, "true").toString());
                }))));
    }

    @Override
    public boolean appliesTo(EvalContext context) {
        // we can add more methods here in the future
        return M_GENERATE_MODEL_DEPRECATED.equals(context.getName());
    }
}

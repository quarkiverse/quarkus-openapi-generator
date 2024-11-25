package io.quarkiverse.openapi.generator.deployment.template;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.quarkus.qute.EvalContext;
import io.quarkus.qute.Expression;

final class ExprEvaluator {

    private ExprEvaluator() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T evaluate(EvalContext context, Expression expression) throws ExecutionException, InterruptedException {
        return (T) context.evaluate(expression).toCompletableFuture().get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] evaluate(EvalContext context, List<Expression> expressions, Class<T> type)
            throws ExecutionException, InterruptedException {
        T[] results = (T[]) java.lang.reflect.Array.newInstance(type, expressions.size());

        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            T result = type.cast(context.evaluate(expression).toCompletableFuture().get());
            results[i] = result;
        }

        return results;
    }

}

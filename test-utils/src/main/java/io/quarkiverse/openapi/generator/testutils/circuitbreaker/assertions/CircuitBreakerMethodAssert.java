package io.quarkiverse.openapi.generator.testutils.circuitbreaker.assertions;

import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.AbstractAssert;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;

public final class CircuitBreakerMethodAssert extends AbstractAssert<CircuitBreakerMethodAssert, MethodDeclaration> {

    private static final String CIRCUIT_BREAKER_ANNOTATION_NAME = "CircuitBreaker";

    private CircuitBreakerMethodAssert(MethodDeclaration actual) {
        super(actual, CircuitBreakerMethodAssert.class);
    }

    public static CircuitBreakerMethodAssert assertThat(MethodDeclaration actual) {
        return new CircuitBreakerMethodAssert(actual);
    }

    public CircuitBreakerMethodAssert hasCircuitBreakerAnnotation() {
        if (actual.getAnnotationByName(CIRCUIT_BREAKER_ANNOTATION_NAME).isEmpty()) {
            failWithMessage("Method named %s is expected to have the CircuitBreaker annotation, but it doesn't",
                    actual.getNameAsString());
        }
        return this;
    }

    public CircuitBreakerMethodAssert doesNotHaveCircuitBreakerAnnotation() {
        if (actual.getAnnotationByName(CIRCUIT_BREAKER_ANNOTATION_NAME).isPresent()) {
            failWithMessage("Method named %s is expected to not have the CircuitBreaker annotation, but it does",
                    actual.getNameAsString());
        }
        return this;
    }

    public CircuitBreakerMethodAssert doesNotHaveAnyCircuitBreakerAttribute() {
        Optional<AnnotationExpr> annotation = actual.getAnnotationByName(CIRCUIT_BREAKER_ANNOTATION_NAME);

        if (annotation.isPresent()) {
            Stream<MemberValuePair> attributes = annotation.orElseThrow().getChildNodes().stream()
                    .filter(MemberValuePair.class::isInstance)
                    .map(MemberValuePair.class::cast);

            if (attributes.findAny().isPresent()) {
                failWithMessage("CircuitBreaker annotations at method %s is expected not to have any attributes, but it does",
                        actual.getNameAsString());
            }
        }

        return this;
    }
}

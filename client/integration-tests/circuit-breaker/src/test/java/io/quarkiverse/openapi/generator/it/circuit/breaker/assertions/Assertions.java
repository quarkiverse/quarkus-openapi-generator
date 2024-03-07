package io.quarkiverse.openapi.generator.it.circuit.breaker.assertions;

import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkiverse.openapi.generator.testutils.circuitbreaker.assertions.CircuitBreakerMethodAssert;

public final class Assertions extends org.assertj.core.api.Assertions {

    private Assertions() {
    }

    public static CircuitBreakerMethodAssert assertThat(MethodDeclaration actual) {
        return CircuitBreakerMethodAssert.assertThat(actual);
    }
}

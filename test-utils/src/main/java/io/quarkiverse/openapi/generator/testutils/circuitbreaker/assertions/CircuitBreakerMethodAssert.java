package io.quarkiverse.openapi.generator.testutils.circuitbreaker.assertions;

import java.util.Optional;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.error.ShouldHaveToString;

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

    public CircuitBreakerMethodAssert hasSkipOnAsString(String expectedToString) {
        return annotationAttributeHasToString("skipOn", expectedToString);
    }

    public CircuitBreakerMethodAssert hasFailOnAsString(String expectedToString) {
        return annotationAttributeHasToString("failOn", expectedToString);
    }

    public CircuitBreakerMethodAssert hasDelayAsString(String expectedToString) {
        return annotationAttributeHasToString("delay", expectedToString);
    }

    public CircuitBreakerMethodAssert hasDelayUnitAsString(String expectedToString) {
        return annotationAttributeHasToString("delayUnit", expectedToString);
    }

    public CircuitBreakerMethodAssert hasRequestVolumeThresholdAsString(String expectedToString) {
        return annotationAttributeHasToString("requestVolumeThreshold", expectedToString);
    }

    public CircuitBreakerMethodAssert hasFailureRatioAsString(String expectedToString) {
        return annotationAttributeHasToString("failureRatio", expectedToString);
    }

    public CircuitBreakerMethodAssert hasSuccessThresholdAsString(String expectedToString) {
        return annotationAttributeHasToString("successThreshold", expectedToString);
    }

    private CircuitBreakerMethodAssert annotationAttributeHasToString(String attributeName, String expectedToString) {
        hasCircuitBreakerAnnotation();

        final AnnotationExpr annotation = actual.getAnnotationByName(CIRCUIT_BREAKER_ANNOTATION_NAME).orElseThrow();

        final Optional<MemberValuePair> attribute = annotation.getChildNodes().stream()
                .filter(MemberValuePair.class::isInstance)
                .map(MemberValuePair.class::cast)
                .filter(p -> p.getNameAsString().equals(attributeName))
                .findAny();

        if (attribute.isEmpty()) {
            failWithMessage("Attribute named %s is expected to have toString equals %s, but the attribute is not present",
                    attributeName, expectedToString);

            return this;
        }

        final String actualToString = attribute.orElseThrow().getValue().toString();

        if (!actualToString.equals(expectedToString)) {
            failWithActualExpectedAndMessage(
                    actualToString,
                    expectedToString,
                    ShouldHaveToString.shouldHaveToString(actualToString, expectedToString).create());
        }

        return this;
    }

    public CircuitBreakerMethodAssert doesNotHaveDelay() {
        return doesNotHaveAnnotationAttribute("delay");
    }

    public CircuitBreakerMethodAssert doesNotHaveDelayUnit() {
        return doesNotHaveAnnotationAttribute("delayUnit");
    }

    public CircuitBreakerMethodAssert doesNotHaveFailOn() {
        return doesNotHaveAnnotationAttribute("failOn");
    }

    public CircuitBreakerMethodAssert doesNotHaveFailureRatio() {
        return doesNotHaveAnnotationAttribute("failureRatio");
    }

    public CircuitBreakerMethodAssert doesNotHaveRequestVolumeThreshold() {
        return doesNotHaveAnnotationAttribute("requestVolumeThreshold");
    }

    public CircuitBreakerMethodAssert doesNotHaveSkipOn() {
        return doesNotHaveAnnotationAttribute("skipOn");
    }

    public CircuitBreakerMethodAssert doesNotHaveSuccessThreshold() {
        return doesNotHaveAnnotationAttribute("successThreshold");
    }

    private CircuitBreakerMethodAssert doesNotHaveAnnotationAttribute(String attributeName) {
        Optional<AnnotationExpr> annotation = actual.getAnnotationByName(CIRCUIT_BREAKER_ANNOTATION_NAME);

        if (annotation.isPresent()) {
            Optional<MemberValuePair> attribute = annotation.orElseThrow().getChildNodes().stream()
                    .filter(MemberValuePair.class::isInstance)
                    .map(MemberValuePair.class::cast)
                    .filter(p -> p.getNameAsString().equals(attributeName)).findAny();

            if (attribute.isPresent()) {
                failWithMessage(
                        "Method named %s is expected not to have the %s attribute on its CircuitBreaker annotation, but it has",
                        actual.getNameAsString(), attributeName);
            }
        }

        return this;
    }
}

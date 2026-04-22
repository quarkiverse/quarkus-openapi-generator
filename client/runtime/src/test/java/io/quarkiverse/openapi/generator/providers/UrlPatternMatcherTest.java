package io.quarkiverse.openapi.generator.providers;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UrlPatternMatcherTest {

    @ParameterizedTest
    @MethodSource("providePathsThatMatch")
    void verifyPathsMatch(final String pathPattern, final String requestPath) {
        UrlPatternMatcher pattern = new UrlPatternMatcher(pathPattern);
        Assertions.assertTrue(pattern.matches(requestPath));
    }

    @ParameterizedTest
    @MethodSource("providePathsThatNotMatch")
    void verifyPathsNotMatch(final String pathPattern, final String requestPath) {
        UrlPatternMatcher pattern = new UrlPatternMatcher(pathPattern);
        Assertions.assertFalse(pattern.matches(requestPath));
    }

    private static Stream<Arguments> providePathsThatMatch() {
        return Stream.of(
                Arguments.of("/pets/{id}", "/pets/1"),
                Arguments.of("/{id}", "/1"),
                Arguments.of("/{id}/pets/", "/1/pets/"),
                Arguments.of("/{id}/pets", "/1/pets"),
                Arguments.of("/{id}", "/1"),
                Arguments.of("/{id}/pets/{id2}", "/1/pets/2"),
                Arguments.of("/pets/{id}", "/pets/1?q=1&q2=2"),
                Arguments.of("/{id}", "/1?q=1&q2=2"),
                Arguments.of("/{id}/pets/", "/1/pets/?q=1&q2=2"),
                Arguments.of("/{id}", "/1?q=1&q2=2"),
                Arguments.of("/{id}/pets/{id2}", "/1/pets/2?q=1&q2=2"),
                Arguments.of("/{id}/{foo}/{id2}", "/1/2/3?q=1&q2=2"),
                Arguments.of("/{id}/{foo}/{id2}", "/1/2/3"),
                Arguments.of("/v2/pets/{id}", "/v2/pets/1"),
                Arguments.of("/pets/{pet-id}/types/{type-id}", "/pets/1/types/2"),
                // Note: Multi-segment path parameters (e.g., Git refs like "heads/feature-a")
                // are not currently supported. These were removed as they represented
                // the security vulnerability fixed in this commit.
                // Future work: Add proper support for x-multi-segment extension
                Arguments.of("pepe/pepa/pepu", "pepe/pepa/pepu"));
    }

    private static Stream<Arguments> providePathsThatNotMatch() {
        return Stream.of(
                Arguments.of("/pets/{id}", "/pes/1"),
                Arguments.of("/{id}/pepe", "/1/2/pep"),
                // Security: path parameters should NOT match across slashes
                // This test demonstrates CVE-2025-XXXXX authentication bypass
                Arguments.of("/repos/{ref}", "/repos/owner/repo"),
                Arguments.of("/api/{version}", "/api/v1/users"),
                Arguments.of("/items/{id}", "/items/123/edit"));
    }

}
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
                Arguments.of("/v2/pets/{id}", "/v2/pets/1"));
    }

}
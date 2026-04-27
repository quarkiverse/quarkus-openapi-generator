package io.quarkiverse.openapi.generator.providers;

import java.util.Set;
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
                // can be enabled via the x-multi-segment extension per parameter.
                // See verifyMultiSegmentMatching tests for examples.
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

    @ParameterizedTest
    @MethodSource("provideMultiSegmentTests")
    void verifyMultiSegmentMatching(final String pathPattern, final Set<String> multiSegmentParams,
            final String requestPath, final boolean shouldMatch) {
        UrlPatternMatcher pattern = new UrlPatternMatcher(pathPattern, multiSegmentParams);
        if (shouldMatch) {
            Assertions.assertTrue(pattern.matches(requestPath),
                    () -> "Expected " + pathPattern + " to match " + requestPath);
        } else {
            Assertions.assertFalse(pattern.matches(requestPath),
                    () -> "Expected " + pathPattern + " NOT to match " + requestPath);
        }
    }

    private static Stream<Arguments> provideMultiSegmentTests() {
        return Stream.of(
                // Single-segment default (secure)
                Arguments.of("/repos/{ref}", Set.of(), "/repos/main", true),
                Arguments.of("/repos/{ref}", Set.of(), "/repos/heads/feature", false),

                // Multi-segment when explicitly enabled
                Arguments.of("/repos/{ref}", Set.of("ref"), "/repos/heads/feature", true),
                Arguments.of("/repos/{ref}", Set.of("ref"), "/repos/heads/feature/fix", true),
                Arguments.of("/repos/{ref}", Set.of("ref"), "/repos/tags/v1.0.0", true),

                // Mixed parameters - one multi, one single
                Arguments.of("/repos/{owner}/{ref}", Set.of("ref"), "/repos/myorg/heads/feature", true),
                Arguments.of("/repos/{owner}/{ref}", Set.of("ref"), "/repos/my/org/main", true),

                // Both parameters multi-segment
                Arguments.of("/repos/{owner}/{ref}", Set.of("owner", "ref"), "/repos/my/org/heads/main", true),

                // Empty set = all single-segment
                Arguments.of("/repos/{owner}/{ref}", Set.of(), "/repos/myorg/main", true),
                Arguments.of("/repos/{owner}/{ref}", Set.of(), "/repos/myorg/heads/main", false),

                // Three parameters, mixed
                Arguments.of("/api/{version}/{resource}/{id}", Set.of("resource"),
                        "/api/v1/users/groups/admin", true),
                Arguments.of("/api/{version}/{resource}/{id}", Set.of("resource"),
                        "/api/v1/v2/users/admin", true));
    }

    @ParameterizedTest
    @MethodSource("provideSecurityRegressionTests")
    void verifySecurityFixRemains(final String pathPattern, final String requestPath) {
        // Without x-multi-segment, these MUST NOT match (CVE protection)
        UrlPatternMatcher pattern = new UrlPatternMatcher(pathPattern, Set.of());
        Assertions.assertFalse(pattern.matches(requestPath),
                () -> "SECURITY: single-segment param incorrectly matched across slashes: "
                        + pathPattern + " matched " + requestPath);
    }

    private static Stream<Arguments> provideSecurityRegressionTests() {
        return Stream.of(
                // Original CVE cases - these must never match
                Arguments.of("/repos/{ref}", "/repos/owner/repo"),
                Arguments.of("/api/{version}", "/api/v1/users"),
                Arguments.of("/items/{id}", "/items/123/edit"),

                // Additional security cases
                Arguments.of("/users/{id}", "/users/admin/delete"),
                Arguments.of("/files/{path}", "/files/etc/passwd"));
    }

}
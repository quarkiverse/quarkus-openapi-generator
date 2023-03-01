package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.codegen.RestEasyImplementationVerifier.RESTEASY_CLASSIC_ARTIFACT_ID;
import static io.quarkiverse.openapi.generator.deployment.codegen.RestEasyImplementationVerifier.RESTEASY_REACTIVE_ARTIFACT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;

import org.junit.jupiter.api.Test;

class RestEasyImplementationVerifierTest {

    @Test
    void testReactive() {
        List<String> dependencies = List.of(
                "dep1",
                "dep2",
                RESTEASY_REACTIVE_ARTIFACT_ID,
                "dep3");

        assertThat(RestEasyImplementationVerifier.get().isRestEasyReactive(dependencies))
                .isTrue();
    }

    @Test
    void testClassic() {
        List<String> dependencies = List.of(
                "dep1",
                "dep2",
                RESTEASY_CLASSIC_ARTIFACT_ID,
                "dep3");

        assertThat(RestEasyImplementationVerifier.get().isRestEasyReactive(dependencies))
                .isFalse();
    }

    @Test
    void testNone() {
        List<String> dependencies = List.of(
                "dep1",
                "dep2",
                "dep3");

        assertThatIllegalStateException()
                .isThrownBy(() -> RestEasyImplementationVerifier.get().isRestEasyReactive(dependencies));
    }
}

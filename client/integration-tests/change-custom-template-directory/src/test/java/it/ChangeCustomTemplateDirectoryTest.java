package it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.simple.openapi.api.ReactiveGreetingResourceApi;

class ChangeCustomTemplateDirectoryTest {

    @Test
    void apiIsBeingGenerated() throws NoSuchMethodException {
        assertThat(ReactiveGreetingResourceApi.class.getMethod("myCustomMethod")).isNotNull();
    }
}

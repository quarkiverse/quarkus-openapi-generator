package io.quarkiverse.openapi.generator.deployment.wrapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusJavaClientCodegenTest {

    @Test
    void when_symbol_contains_special_characters_should_replace_correctly() {
        // arrange
        QuarkusJavaClientCodegen codegen = new QuarkusJavaClientCodegen();

        // act
        String symbolName = codegen.getSymbolName("/status/addressStatus");

        // assert
        Assertions.assertThat(symbolName).isEqualToIgnoringCase("SLASH_STATUS_Slash_ADDRESSSTATUS");
    }

    @Test
    void when_contains_special_characters_and_the_input_has_length_equal_1_should() {
        // arrange
        QuarkusJavaClientCodegen codegen = new QuarkusJavaClientCodegen();

        // act
        String symbolName = codegen.getSymbolName("$");

        // assert
        Assertions.assertThat(symbolName).isEqualToIgnoringCase("DOLLAR_symbol");
    }

    @Test
    void when_special_character_is_the_first_should_replace_correctly() {
        // arrange
        QuarkusJavaClientCodegen codegen = new QuarkusJavaClientCodegen();

        // act
        String symbolName = codegen.getSymbolName("/users");

        // assert
        Assertions.assertThat(symbolName).isEqualToIgnoringCase("SLASH_users");
    }

    @Test
    void when_symbol_contains_special_characters_in_the_end_should_replace_correctly() {
        // arrange
        QuarkusJavaClientCodegen codegen = new QuarkusJavaClientCodegen();

        // act
        String symbolName = codegen.getSymbolName("/status/addressStatus/");

        // assert
        Assertions.assertThat(symbolName).isEqualToIgnoringCase("SLASH_STATUS_Slash_ADDRESSSTATUS_Slash");
    }
}

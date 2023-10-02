package io.quarkiverse.openapi.generator.deployment.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkiverse.openapi.generator.deployment.assertions.Assertions;

class QuarkusJavaClientCodegenTest {

    @ParameterizedTest
    @CsvSource({
            "/status/addressStatus,SLASH_STATUS_SLASH_ADDRESSSTATUS",
            "$,DOLLAR_SYMBOL",
            "/users,SLASH_USERS"
    })
    void toEnumVarName(String value, String expectedVarName) {

        QuarkusJavaClientCodegen quarkusJavaClientCodegen = new QuarkusJavaClientCodegen();

        String varName = quarkusJavaClientCodegen.toEnumVarName(value, "String");

        Assertions.assertThat(varName).isEqualTo(expectedVarName);
    }
}

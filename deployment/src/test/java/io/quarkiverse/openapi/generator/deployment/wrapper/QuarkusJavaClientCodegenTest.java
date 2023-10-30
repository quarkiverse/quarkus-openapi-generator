package io.quarkiverse.openapi.generator.deployment.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkiverse.openapi.generator.deployment.assertions.Assertions;

class QuarkusJavaClientCodegenTest {

    @ParameterizedTest
    @CsvSource({
            "/status/addressStatus,String,SLASH_STATUS_SLASH_ADDRESSSTATUS",
            "$,String,DOLLAR_SYMBOL",
            "/users,String,SLASH_USERS",
            "'  ',String,EMPTY",
            "123456,String,_123456",
            "quarkus_resources,String,QUARKUS_RESOURCES",
            "123456,Integer,NUMBER_123456", // old behavior
            "123+123,Long,NUMBER_123PLUS_123", // old behavior,
            "M123,String,M123",
            "MA456,String,MA456",
            "P1,String,P1",
    })
    void toEnumVarName(String value, String dataType, String expectedVarName) {

        QuarkusJavaClientCodegen quarkusJavaClientCodegen = new QuarkusJavaClientCodegen();

        String varName = quarkusJavaClientCodegen.toEnumVarName(value, dataType);

        Assertions.assertThat(varName).isEqualTo(expectedVarName);
    }
}
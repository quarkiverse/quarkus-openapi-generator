package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Method;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BeanParamConfigurationTest {
    @Test
    void checkPageableMustDefinePageableQueryParam() {
        assertThatCode(() -> Class.forName(
                "org.openapi.quarkus.openapi_generate_model_for_usage_as_bean_param_yaml.model.Pageable$PageableQueryParam"))
                .doesNotThrowAnyException();
    }

    @Test
    void checkCustomerApiMustDefineSearchCustomersWithPageableBeanParam() {
        assertThatCode(() -> {
            Class<?> apiClass = Class.forName(
                    "org.openapi.quarkus.openapi_generate_model_for_usage_as_bean_param_yaml.api.CustomerApi");
            Method searchCustomersMethod = apiClass.getMethod("searchCustomers",
                    Class.forName(
                            "org.openapi.quarkus.openapi_generate_model_for_usage_as_bean_param_yaml.model.Pageable$PageableQueryParam"));
            assertThat(searchCustomersMethod).isNotNull();
            assertThat(searchCustomersMethod.getParameterAnnotations()[0][1]).isInstanceOf(BeanParam.class);
        });
    }

    @Test
    void checkEchoMustNotDefineEchoQueryParam() {
        assertThatCode(() -> Class.forName(
                "org.openapi.quarkus.openapi_dont_generate_model_for_usage_as_bean_param_yaml.model.Pageable$PageableQueryParam"))
                .isInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void checkCustomerApiMustDefineSearchCustomersWithPageableQueryParam() {
        assertThatCode(() -> {
            Class<?> apiClass = Class.forName(
                    "org.openapi.quarkus.openapi_dont_generate_model_for_usage_as_bean_param_yaml.api.CustomerApi");
            Method searchCustomersMethod = apiClass.getMethod("searchCustomers",
                    Class.forName(
                            "org.openapi.quarkus.openapi_dont_generate_model_for_usage_as_bean_param_yaml.model.Pageable$PageableQueryParam"));
            assertThat(searchCustomersMethod).isNotNull();
            assertThat(searchCustomersMethod.getParameterAnnotations()[0][1]).isInstanceOf(QueryParam.class);
        });
    }
}

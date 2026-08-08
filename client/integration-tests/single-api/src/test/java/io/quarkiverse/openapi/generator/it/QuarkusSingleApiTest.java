package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;

import org.acme.openapi.shop.api.OrderManagementApi;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusSingleApiTest {

    @Test
    void allOperationsAreGeneratedInSingleApiClass() {
        assertThat(OrderManagementApi.class.getMethods())
                .extracting(Method::getName)
                .contains("createOrder", "getOrderById", "getCustomerById", "processPayment");
    }

    @Test
    void perTagApiClassesAreNotGenerated() {
        assertThatThrownBy(() -> Class.forName("org.acme.openapi.shop.api.OrdersApi"))
                .isInstanceOf(ClassNotFoundException.class);
        assertThatThrownBy(() -> Class.forName("org.acme.openapi.shop.api.CustomersApi"))
                .isInstanceOf(ClassNotFoundException.class);
        assertThatThrownBy(() -> Class.forName("org.acme.openapi.shop.api.PaymentsApi"))
                .isInstanceOf(ClassNotFoundException.class);
    }
}

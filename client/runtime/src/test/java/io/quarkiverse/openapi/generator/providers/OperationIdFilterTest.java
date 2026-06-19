package io.quarkiverse.openapi.generator.providers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientRequestContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.openapi.generator.markers.OperationMarker;

@ExtendWith(MockitoExtension.class)
class OperationIdFilterTest {

    // Test interface with OperationMarker annotation
    interface TestApi {
        @GET
        @Path("/api/test")
        @OperationMarker(name = "auth", openApiSpecId = "test", operationId = "testOp", method = "GET", path = "/api/test")
        String testMethod();
    }

    @Test
    void shouldExtractOperationMetadataWhenMethodIsAvailable() throws IOException, NoSuchMethodException {
        // Given
        OperationIdFilter filter = new OperationIdFilter();
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        Method method = TestApi.class.getMethod("testMethod");

        // Mock the MicroProfile property
        when(requestContext.getProperty("org.eclipse.microprofile.rest.client.invokedMethod"))
                .thenReturn(method);

        // When
        filter.filter(requestContext);

        // Then - verify properties were set
        org.mockito.Mockito.verify(requestContext).setProperty(
                BaseCompositeAuthenticationProvider.OPERATION_PATH_PROPERTY,
                "/api/test");
        org.mockito.Mockito.verify(requestContext).setProperty(
                BaseCompositeAuthenticationProvider.OPERATION_METHOD_PROPERTY,
                "GET");
        org.mockito.Mockito.verify(requestContext).setProperty(
                BaseCompositeAuthenticationProvider.OPERATION_ID_PROPERTY,
                "testOp");
    }

    @Test
    void shouldNotSetPropertiesWhenMethodIsNull() throws IOException {
        // Given
        OperationIdFilter filter = new OperationIdFilter();
        ClientRequestContext requestContext = mock(ClientRequestContext.class);

        // No method property set
        when(requestContext.getProperty("org.eclipse.microprofile.rest.client.invokedMethod"))
                .thenReturn(null);

        // When
        filter.filter(requestContext);

        // Then - verify no properties were set
        org.mockito.Mockito.verify(requestContext, org.mockito.Mockito.never())
                .setProperty(org.mockito.Mockito.anyString(), org.mockito.Mockito.any());
    }

    @Test
    void shouldNotSetPropertiesWhenNoOperationMarker() throws IOException, NoSuchMethodException {
        // Given
        OperationIdFilter filter = new OperationIdFilter();
        ClientRequestContext requestContext = mock(ClientRequestContext.class);

        // Method without OperationMarker
        Method method = String.class.getMethod("toString");
        when(requestContext.getProperty("org.eclipse.microprofile.rest.client.invokedMethod"))
                .thenReturn(method);

        // When
        filter.filter(requestContext);

        // Then - verify no properties were set
        org.mockito.Mockito.verify(requestContext, org.mockito.Mockito.never())
                .setProperty(org.mockito.Mockito.anyString(), org.mockito.Mockito.any());
    }
}

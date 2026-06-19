package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.lang.reflect.Method;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;

import io.quarkiverse.openapi.generator.markers.OperationMarker;

/**
 * Client request filter that extracts operation metadata from the {@link OperationMarker}
 * annotation on the invoked REST client method and sets it as request context properties.
 * <p>
 * SECURITY (GHSA-fqh4-5f48-9j28): This filter enables path-template-based authentication
 * matching to prevent credential leakage to unintended endpoints. By matching on the exact
 * path template (e.g., "/repos/{ref}") rather than using regex patterns, literal sibling paths
 * (e.g., "/repos/health") will not match and credentials won't be leaked.
 * <p>
 * This filter must run BEFORE the authentication filter ({@link BaseCompositeAuthenticationProvider})
 * to ensure the operation path and method are available for authentication decisions.
 * Priority is set to run before AUTHENTICATION (2000).
 * <p>
 * This class is marked as ApplicationScoped and @Provider for Quarkus REST Client compatibility.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 1000) // Run before authentication filters
public class OperationIdFilter implements ClientRequestFilter {

    /**
     * Property name defined by MicroProfile Rest Client specification.
     * This property contains the java.lang.reflect.Method that was invoked on the REST client interface.
     */
    private static final String INVOKED_METHOD_PROPERTY = "org.eclipse.microprofile.rest.client.invokedMethod";

    /**
     * Alternative property names to try for different REST client implementations.
     */
    private static final String[] ALTERNATIVE_METHOD_PROPERTIES = {
            "io.quarkus.rest.client.invokedMethod",
            "io.quarkus.rest.runtime.client.QuarkusRestClientRequestContext.invokedMethod",
            "org.jboss.resteasy.reactive.client.impl.RestClientRequestContext.method",
            "method",
            "resourceMethod"
    };

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // Get the invoked method using the standard MicroProfile Rest Client property
        Method method = getInvokedMethod(requestContext);

        if (method == null) {
            // No method available - cannot extract operation metadata
            // This is expected when the INVOKED_METHOD_PROPERTY is not set
            return;
        }

        // Check for OperationMarker annotation
        OperationMarker[] markers = method.getAnnotationsByType(OperationMarker.class);
        if (markers.length == 0) {
            // No operation marker - nothing to extract
            return;
        }

        // Use the first marker (multiple markers exist when an operation has multiple auth methods)
        OperationMarker marker = markers[0];

        // Set path template and method for matching
        // This is more robust than operationId (which is optional in OpenAPI)
        // and avoids regex pattern matching vulnerabilities
        requestContext.setProperty(
                BaseCompositeAuthenticationProvider.OPERATION_PATH_PROPERTY,
                marker.path());
        requestContext.setProperty(
                BaseCompositeAuthenticationProvider.OPERATION_METHOD_PROPERTY,
                marker.method());

        // Also set operationId if available (for backward compatibility and debugging)
        if (marker.operationId() != null && !marker.operationId().isEmpty()) {
            requestContext.setProperty(
                    BaseCompositeAuthenticationProvider.OPERATION_ID_PROPERTY,
                    marker.operationId());
        }
    }

    /**
     * Retrieves the invoked method from the request context.
     * Tries multiple approaches to support both RESTEasy Classic and Reactive.
     */
    private Method getInvokedMethod(ClientRequestContext requestContext) {
        // Try standard MicroProfile property first
        Object methodProperty = requestContext.getProperty(INVOKED_METHOD_PROPERTY);
        if (methodProperty instanceof Method) {
            return (Method) methodProperty;
        }

        // Try alternative property names
        for (String propertyName : ALTERNATIVE_METHOD_PROPERTIES) {
            methodProperty = requestContext.getProperty(propertyName);
            if (methodProperty instanceof Method) {
                return (Method) methodProperty;
            }
        }

        // Fallback: Try extracting from the request context implementation using reflection
        return extractMethodViaReflection(requestContext);
    }

    /**
     * Attempts to extract the invoked method via reflection on the request context.
     * This is a fallback for environments where the standard property is not set.
     */
    private Method extractMethodViaReflection(ClientRequestContext requestContext) {
        try {
            // Try to get the method from the request context implementation
            // This is implementation-specific and may not work in all environments
            Class<?> contextClass = requestContext.getClass();

            // Try common getter methods that might return the invoked method
            String[] possibleAccessors = {
                    "getMethod",
                    "getInvokedMethod",
                    "getResourceMethod",
                    "getClientInvoker",
                    "getInvocation"
            };

            for (String accessor : possibleAccessors) {
                try {
                    java.lang.reflect.Method getterMethod = contextClass.getMethod(accessor);
                    Object result = getterMethod.invoke(requestContext);

                    // Direct Method return
                    if (result instanceof Method) {
                        return (Method) result;
                    }

                    // Try to extract Method from the result object
                    if (result != null) {
                        Method extracted = extractMethodFromObject(result);
                        if (extracted != null) {
                            return extracted;
                        }
                    }
                } catch (NoSuchMethodException ignored) {
                    // Try next accessor
                }
            }

            // Try accessing common field names
            String[] possibleFields = { "method", "invokedMethod", "resourceMethod", "clientInvoker" };
            for (String fieldName : possibleFields) {
                try {
                    java.lang.reflect.Field field = contextClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object result = field.get(requestContext);

                    if (result instanceof Method) {
                        return (Method) result;
                    }

                    if (result != null) {
                        Method extracted = extractMethodFromObject(result);
                        if (extracted != null) {
                            return extracted;
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                    // Try next field
                }
            }
        } catch (Exception e) {
            // Reflection failed, return null
        }

        return null;
    }

    /**
     * Attempts to extract a Method from an object that might contain it.
     */
    private Method extractMethodFromObject(Object obj) {
        try {
            Class<?> objClass = obj.getClass();

            // Try getMethod() on the object
            try {
                java.lang.reflect.Method getter = objClass.getMethod("getMethod");
                Object result = getter.invoke(obj);
                if (result instanceof Method) {
                    return (Method) result;
                }
            } catch (NoSuchMethodException ignored) {
            }

            // Try accessing method field
            try {
                java.lang.reflect.Field field = objClass.getDeclaredField("method");
                field.setAccessible(true);
                Object result = field.get(obj);
                if (result instanceof Method) {
                    return (Method) result;
                }
            } catch (NoSuchFieldException ignored) {
            }
        } catch (Exception e) {
            // Ignore
        }

        return null;
    }
}

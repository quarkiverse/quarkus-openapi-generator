package io.quarkiverse.openapi.generator.providers;

import java.util.Map;

/**
 * Base interface for configuration of authentication providers.
 * Extensions are generated with the correct [`ConfigMapping`](https://quarkus.io/guides/config-mappings) annotations based on
 * the client property package.
 *
 * <h4>Example</h4>
 * <p>
 * ```properties
 * quarkus.openapi-generator.codegen.spec."petstore.json".base-package=org.acme.petstore
 * <p>
 * org.acme.petstore.security.auth.basic_http/username=alice
 * org.acme.petstore.security.auth.basic_http/password=jackson
 * ```
 * Based on the `base-package` property, one can append `.security.auth.[security_scheme_name]/[property]` to configure the
 * application.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.1.0#security-scheme-object">OpenAPI Spec - Security Scheme</a> for supported
 *      configurations.
 * @see <a href="https://quarkus.io/guides/config-mappings">Quarkus - Mapping Configuration to Objects</a>
 */
public interface AuthProvidersConfig {
    Map<String, String> auth();
}

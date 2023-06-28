# Quarkus - OpenAPI Generator

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-16-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
[![Build](<https://img.shields.io/github/actions/workflow/status/quarkiverse/quarkus-openapi-generator/build.yml?branch=quarkus2&logo=GitHub&style=flat-square>)](https://github.com/quarkiverse/quarkus-openapi-generator/actions?query=workflow%3ABuild)
[![Maven Central](https://img.shields.io/maven-central/v/io.quarkiverse.openapi.generator/quarkus-openapi-generator.svg?label=Maven%20Central&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.openapi.generator/quarkus-openapi-generator)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)


> **⚠️** This is the instructions for the latest SNAPSHOT version (main branch). Please, see the [latest **released** documentation](https://github.com/quarkiverse/quarkus-openapi-generator/blob/0.12.0/README.md) if you are looking for instructions.

> **⚠️** Version 2.x.x of this extension (`main` branch) supports Quarkus 3, and version 1.x.x (`quarkus2` branch) supports Quarkus 2.

Quarkus' extension for generation of [Rest Clients](https://quarkus.io/guides/rest-client) based on OpenAPI specification files.

This extension is based on the [OpenAPI Generator Tool](https://openapi-generator.tech/). Please consider donation to help them maintain the
project: https://opencollective.com/openapi_generator/donate

This extension is for REST code generation for client side only. If you're looking for code generation for the server side, please take a look at the [Quarkus Apicurio Extension](https://github.com/Apicurio/apicurio-codegen/tree/main/quarkus-extension).

## Getting Started

Add the following dependency to your project's `pom.xml` file:

> **⚠️** Version 2.x.x of this extension supports Quarkus 3, and version 1.x.x supports Quarkus 2.

```xml

<dependency>
  <groupId>io.quarkiverse.openapi.generator</groupId>
  <artifactId>quarkus-openapi-generator</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

You will also need to add or update the `quarkus-maven-plugin` configuration with the following:

> **⚠️**
You probably already have this configuration if you created your application with [Code Quarkus](https://code.quarkus.io/). That said, double-check your configuration not to add another `plugin` entry.

```xml

<plugin>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-maven-plugin</artifactId>
  <extensions>true</extensions>
  <executions>
    <execution>
      <goals>
        <goal>build</goal>
        <goal>generate-code</goal>
        <goal>generate-code-tests</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Now, create the directory `openapi` under your `src/main/` path and add the OpenAPI spec files there. We support JSON, YAML and YML extensions.

If you want to change the directory where OpenAPI files must be found, use the property `quarkus.openapi-generator.codegen.input-base-dir`.
IMPORTANT: it is relative to the project base directory. For example, if you have a project called `MyJavaProject` and decide to place them in `MyJavaProject/openapi-definitions`, use the following property: 

```properties
quarkus.openapi-generator.codegen.input-base-dir=openapi-definitions
```

To fine tune the configuration for each spec file, add the following entry to your properties file. In this example, our spec file is in `src/main/openapi/petstore.json`:

```properties
quarkus.openapi-generator.codegen.spec.petstore_json.additional-model-type-annotations=@org.test.Foo;@org.test.Bar
```

If a base package name is not provided, it will be used the default `org.openapi.quarkus.<filename>`. For example, `org.openapi.quarkus.petstore_json`.

Configuring `additional-model-type-annotations` will add all annotations to the generated model files (extra details can be found in [OpenApi Generator Doc](https://openapi-generator.tech/docs/generators/java/#config-options)).

> **⚠️** Note that the file name`petstore_json`is used to configure the specific information for each spec. We follow the [Environment Variables Mapping Rules](https://github.com/eclipse/microprofile-config/blob/master/spec/src/main/asciidoc/configsources.asciidoc#environment-variables-mapping-rules) from Microprofile Configuration to sanitize the OpenAPI spec filename. Any non-alphabetic characters are replaced by an underscore `_`.

Run `mvn compile` to generate your classes in `target/generated-sources/open-api-json` path:

```
- org.acme.openapi
  - api
    - PetApi.java
    - StoreApi.java
    - UserApi.java
  - model
    - Address.java
    - Category.java
    - Customer.java
    - ModelApiResponse.java
    - Order.java
    - Pet.java
    - Tag.java
    - User.java
```

You can reference the generated code in your project, for example:

```java
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.openapi.api.PetApi;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Produces(MediaType.APPLICATION_JSON)
@Path("/petstore")
public class PetResource {

    @RestClient
    @Inject
    PetApi petApi;
}
```

See the [integration-tests](integration-tests) module for more information of how to use this extension. Please be advised that the extension is on experimental, early development stage.

## RESTEasy Reactive and Classic support

You can use the `quarkus-openapi-generator` with REST Client Classic or REST Client Reactive respectively. To do so add either the classic or reactive jackson dependency to your project's `pom.xml` file:

### RESTEasy Classic

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```
> **⚠️** After Version 1.2.1 / 2.1.1 you need to declare the above dependency explicitly! Even if you stay with the REST Client Classic implementation!

### RESTEasy Reactive

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-client-reactive-jackson</artifactId>
</dependency>
```

For both implementations, the generated code is always blocking code.

When using RESTEasy Reactive:
  - The client must not declare multiple MIME-TYPES with `@Consumes`
  - You might need to implement a `ParamConverter` for each complex type

## Returning `Response` objects

By default, this extension generates the methods according to their returning models based on the [OpenAPI specification Schema Object](https://spec.openapis.org/oas/v3.1.0#schema-object). If you want to return `javax.ws.rs.core.Response` instead, you can set the `return-response` property to `true`.

### Example

Given you want to return `javax.ws.rs.core.Response` for the `my-openapi.yaml` file, you must add the following to your `application.properties` file:

```properties
quarkus.openapi-generator.codegen.spec.my_openapi_yaml.return-response=true
```

## Logging

Since the most part of this extension work is in the `generate-code` execution phase of the Quarkus Maven's plugin, the log configuration must be set in the Maven context. When building your project, add `-Dorg.slf4j.simpleLogger.log.org.openapitools=off` to the `mvn` command to reduce the internal generator noise. For example:

```shell
 mvn clean install -Dorg.slf4j.simpleLogger.log.org.openapitools=off
```

For more information, see the [Maven Logging Configuration](https://maven.apache.org/maven-logging.html) guide.

## Filtering OpenAPI Specification Files

By default, the extension will process every OpenAPI specification file in the given path.
To limit code generation to only a specific set of OpenAPI specification files, you can set the `quarkus.openapi-generator.codegen.include` property. 
For instance, if you want to limit code generation for `include-openapi.yaml` and `include-openapi-2.yaml` files, you need to define the property like:

```properties
quarkus.openapi-generator.codegen.include=include-openapi.yaml,include-openapi-2.yaml
```

If you prefer to specify which files you want to skip, you can set the `quarkus.openapi-generator.codegen.exclude` property.
For instance, if you want to skip code generation for `exclude-openapi.yaml` and `exclude-openapi-2.yaml` files, you need to define the property like:

```properties
quarkus.openapi-generator.codegen.exclude=exclude-openapi.yaml,exclude-openapi-2.yaml
```

IMPORTANT: `exclude` supersedes `include`, meaning that if a file is in both property it will NOT be analysed.

See the module [ignore](integration-tests/ignore) for an example of how to use this feature. 

## Authentication Support

If your OpenAPI specification file has `securitySchemes` [definitions](https://spec.openapis.org/oas/v3.1.0#security-scheme-object), the inner generator
will [register `ClientRequestFilter`providers](https://download.eclipse.org/microprofile/microprofile-rest-client-2.0/microprofile-rest-client-spec-2.0.html#_provider_declaration) for you to
implement the given authentication mechanism.

To provide the credentials for your application, you can use the [Quarkus configuration support](https://quarkus.io/guides/config). The configuration key is composed using this
pattern: `quarkus.openapi-generator.[filename].auth.[security_scheme_name].[auth_property_name]`. Where:

- `filename` is the sanitized name of file containing the OpenAPI spec, for example `petstore_json`.
- `security_scheme_name` is the sanitized name of the [security scheme object definition](https://spec.openapis.org/oas/v3.1.0#security-scheme-object) in the OpenAPI file. Given the following excerpt, we
  have `api_key` and `basic_auth` security schemes:

```json
{
  "securitySchemes": {
    "api_key": {
      "type": "apiKey",
      "name": "api_key",
      "in": "header"
    },
    "basic_auth": {
      "type": "http",
      "scheme": "basic"
    }
  }
}
```
> **⚠️** Note that the securityScheme name used to configure the specific information for each spec is sanitized using the same rules as for the file names.

- `auth_property_name` varies depending on the authentication provider. For example, for Basic Authentication we have `username` and `password`. See the following sections for more details.

> Tip: on production environments you will likely to use [HashiCorp Vault](https://quarkiverse.github.io/quarkiverse-docs/quarkus-vault/dev/index.html) or [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/) to provide this information for your application.

If the OpenAPI specification file has `securitySchemes` definitions, but no [Security Requirement Object](https://spec.openapis.org/oas/v3.1.0#security-requirement-object) definitions, the generator can be configured to create these by default. In this case, for all operations without a security requirement the default one will be created. Note that the property value needs to match the name of a security scheme object definition, eg. `api_key` or `basic_auth` in the `securitySchemes` list above.

| Description          | Property Key                                                   | Example                                              |
| -------------------- | -------------------------------------------------------------- | ---------------------------------------------------- |
| Create security for the referenced security scheme | `quarkus.openapi-generator.codegen.default.security.scheme` | `quarkus.openapi-generator.codegen.default.security.scheme=api_key` |

See the module [security](integration-tests/security) for an example of how to use this feature.

### Basic HTTP Authentication

For Basic HTTP Authentication, these are the supported configurations:

| Description          | Property Key                                                   | Example                                              |
| -------------------- | -------------------------------------------------------------- | ---------------------------------------------------- |
| Username credentials | `quarkus.openapi-generator.[filename].auth.[security_scheme_name].username` | `quarkus.openapi-generator.petstore_json.auth.basic_auth.username` |
| Password credentials | `quarkus.openapi-generator.[filename].auth.[security_scheme_name].password` | `quarkus.openapi-generator.petstore_json.auth.basic_auth-password` |

### Bearer Token Authentication

For Bearer Token Authentication, these are the supported configurations:

| Description  | Property Key                                                       | Example                                                  |
| -------------| ------------------------------------------------------------------ | -------------------------------------------------------- |
| Bearer Token | `quarkus.openapi-generator.[filename].auth.[security_scheme_name].bearer-token` | `quarkus.openapi-generator.petstore_json.auth.bearer.bearer-token` |

### API Key Authentication

Similarly to bearer token, the API Key Authentication also has the token entry key property:

| Description  | Property Key                                                  | Example                                             |
| -------------| --------------------------------------------------------------| --------------------------------------------------- |
| API Key      | `quarkus.openapi-generator.[filename].auth.[security_scheme_name].api-key` | `quarkus.openapi-generator.petstore_json.auth.api_key.api-key` |

The API Key scheme has an additional property that requires where to add the API key in the request token: header, cookie or query. The inner provider takes care of that for you.

### OAuth2 Authentication

The extension will generate a `ClientRequestFilter` capable to add OAuth2 authentication capabilities to the OpenAPI operations that require it. This means that you can use
the [Quarkus OIDC Extension](https://quarkus.io/guides/security-openid-connect-client) configuration to define your authentication flow.

The generated code creates a named `OidcClient` for each [Security Scheme](https://spec.openapis.org/oas/v3.1.0#security-scheme-object) listed in the OpenAPI specification files. For example, given
the following excerpt:

```json
{
  "securitySchemes": {
    "petstore_auth": {
      "type": "oauth2",
      "flows": {
        "implicit": {
          "authorizationUrl": "https://petstore3.swagger.io/oauth/authorize",
          "scopes": {
            "write:pets": "modify pets in your account",
            "read:pets": "read your pets"
          }
        }
      }
    }
  }
}
```

You can configure this `OidcClient` as:

```properties
quarkus.oidc-client.petstore_auth.auth-server-url=https://petstore3.swagger.io/oauth/authorize
quarkus.oidc-client.petstore_auth.discovery-enabled=false
quarkus.oidc-client.petstore_auth.token-path=/tokens
quarkus.oidc-client.petstore_auth.credentials.secret=secret
quarkus.oidc-client.petstore_auth.grant.type=password
quarkus.oidc-client.petstore_auth.grant-options.password.username=alice
quarkus.oidc-client.petstore_auth.grant-options.password.password=alice
quarkus.oidc-client.petstore_auth.client-id=petstore-app
```

The configuration suffix `quarkus.oidc-client.petstore_auth` is exclusive for the schema defined in the specification file and the `schemaName` is sanitized by applying the rules described above.

For this to work you **must** add [Quarkus OIDC Client Filter Extension](https://quarkus.io/guides/security-openid-connect-client#oidc-client-filter) to your project:

RESTEasy Classic:

````xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-oidc-client-filter</artifactId>
</dependency>
````

RESTEasy Reactive:

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-oidc-client-reactive-filter</artifactId>
</dependency>
```

See the module [generation-tests](integration-tests/generation-tests) for an example of how to use this feature.

## Authorization Token Propagation

The authorization token propagation can be used with OpenApi operations secured with a security scheme of type "oauth2" or "bearer".
When configured, you can propagate the authorization tokens passed to your service and the invocations to the REST clients generated by the quarkus-openapi-generator.

Let's see how it works by following a simple example:

Imagine that we have a `updatePet` operation defined in the `petstore.json` specification file and secured with the `petstore_auth` security scheme.
The code below shows a simple example of the usage of this operation in a user-programmed service.

## Headers propagation
Custom headers propagation can be set via MicroProfile configuration `org.eclipse.microprofile.rest.client.propagateHeaders`.
In order to consider this configuration you must force @RegisterClientHeaders to use its default microprofile ClientHeadersFactory implementation. Therefore there is an option `client-headers-factory` where you can set any implementation of ClientHeadersFactory.

| Description  | Property Key                                                               | Example                                                                                                                                                    |
| -------------|----------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Bearer Token | `quarkus.openapi-generator.codegen.spec.[fileName].client-headers-factory` | `quarkus.openapi-generator.codegen.spec.open_weather_yaml.client-headers-factory=org.eclipse.microprofile.rest.client.ext.DefaultClientHeadersFactoryImpl` |

If `client-headers-factory` is set to `none` @RegisterClientHeaders will use its default implicit implementation as in example above.

If no option is set then default generated AuthenticationPropagationHeadersFactory class is used.

```java
import org.acme.api.PetApi;
import org.acme.model.Pet;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * User programmed service.
 */
@Path("/petstore")
public class PetResource {

  /**
   * Inject the rest client generated by the quarkus-openapi-generator.
   */
  @Inject
  @RestClient
  PetApi petApi;

  /**
   * User programmed operation.
   */
  @Path("/pet/{id}")
  @PATCH
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response customUpdatePet(@PathParam("id") long id, PetData petData) {

    // Create a new instance of the Pet class generated by the quarkus-openapi-generator and
    // populate accordingly.
    Pet pet = new Pet();
    pet.setId(id);
    applyDataToPet(pet, petData);

    // Execute the rest call using the generated client.
    // The petstore.json open api spec stays that the "updatePet" operation is secured with the
    // security scheme "petstore_auth".
    petApi.updatePet(pet);

    // Do other required things and finally return something.
    return Response.ok().build();
  }

  public static class PetData {
    // Represents the Pet modifiable data sent to the user programmed service.
  }

  private void applyDataToPet(Pet pet, PetData petData) {
    // Set the corresponding values to the Pet instance.
  }
}
```

Let's see what happens when the PetResource service `customUpdatePet` operation is invoked by a third party.

### Default flow
1) The `customUpdatePet` operation is invoked.
2) An authorization token is obtained using the corresponding `petstore_auth` OidcClient configuration. (for more information see [OAuth2 Authentication](#oauth2-authentication))
3) The authorization token is automatically passed along the PetApi `updatePet` operation execution using an automatically generated request filter, etc.

### Propagation flow
However, there are scenarios where we want to propagate the authorization token that was initially passed to the PetResource service when the `customUpdatePet` operation was invoked instead of having to obtain it by using the `OidcClient`.

1) The user service `customUpdatePet` operation is invoked, and an authorization token is passed by the third party typically by using the HTTP `Authorization` header.
2) The incoming authorization token is automatically passed along the PetApi `updatePet` operation execution according to the user-provided configuration.

> **⚠️** When configured, the token propagation applies to all the operations secured with the same `securityScheme` in the same specification file.

### Propagation flow configuration
The token propagation can be used with type "oauth2" or "bearer" security schemes. Finally, considering that a given security scheme might be configured on a set of operations in the same specification file when configured, it'll apply to all these operations.

| Property Key                                                       | Example                                                  |
| ------------------------------------------------------------------ | -------------------------------------------------------- |
| `quarkus.openapi-generator.[filename].auth.[security_scheme_name].token-propagation=[true,false]` | `quarkus.openapi-generator.petstore_json.auth.petstore_auth.token-propagation=true`<br/>Enables the token propagation for all the operations that are secured with the `petstore_auth` scheme in the `petstore_json` file.
| `quarkus.openapi-generator.[filename].auth.[security_scheme_name].header-name=[http_header_name]` | `quarkus.openapi-generator.petstore_json.auth.petstore_auth.header-name=MyHeaderName`<br/>Says that the authorization token to propagate will be read from the HTTP header `MyHeaderName` instead of the standard HTTP `Authorization` header.

## Circuit Breaker

You can define the [CircuitBreaker annotation from MicroProfile Fault Tolerance](https://microprofile.io/project/eclipse/microprofile-fault-tolerance/spec/src/main/asciidoc/circuitbreaker.asciidoc)
in your generated classes by setting the desired configuration in `application.properties`.

Let's say you have the following OpenAPI definition:

````json
{
  "openapi": "3.0.3",
  "info": {
    "title": "Simple API",
    "version": "1.0.0-SNAPSHOT"
  },
  "paths": {
    "/hello": {
      "get": {
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "text/plain": {
                "schema": {
                  "type": "string"
                }
              }
            }
          }
        }
      }
    },
    "/bye": {
      "get": {
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "text/plain": {
                "schema": {
                  "type": "string"
                }
              }
            }
          }
        }
      }
    }
  }
}
````

And you want to configure Circuit Breaker for the `/bye` endpoint, you can do it in the following way:

Add the [SmallRye Fault Tolerance extension](https://quarkus.io/guides/smallrye-fault-tolerance) to your project's `pom.xml` file:

````xml

<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-fault-tolerance</artifactId>
</dependency>
````

Assuming your Open API spec file is in `src/main/openapi/simple-openapi.json`, add the following configuration to your `application.properties` file:

````properties
# Note that the file name must have only alphabetic characters or underscores (_).
quarkus.openapi-generator.codegen.spec.simple_openapi_json.base-package=org.acme.openapi.simple
# Enables the CircuitBreaker extension for the byeGet method from the DefaultApi class
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/enabled=true
````

With the above configuration, your Rest Clients will be created with a code similar to the following:

````java
package org.acme.openapi.simple.api;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("")
@RegisterRestClient(configKey="simple-openapi_json")
public interface DefaultApi {

    @GET
    @Path("/bye")
    @Produces({ "text/plain" })
    @org.eclipse.microprofile.faulttolerance.CircuitBreaker
    public String byeGet();

    @GET
    @Path("/hello")
    @Produces({ "text/plain" })
    public String helloGet();

}
````

You can also override the default Circuit Breaker configuration by setting the properties
in `application.properties` [just as you would for a traditional MicroProfile application](https://quarkus.io/guides/smallrye-fault-tolerance#runtime-configuration):

````properties
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/failOn=java.lang.IllegalArgumentException,java.lang.NullPointerException
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/skipOn=java.lang.NumberFormatException
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/delay=33
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/delayUnit=MILLIS
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/requestVolumeThreshold=42
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/failureRatio=3.14
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/successThreshold=22
````

See the module [circuit-breaker](integration-tests/circuit-breaker) for an example of how to use this feature.

## Sending multipart/form-data

The rest client also supports request with mime-type multipart/form-data and, if the schema of the request body is known in advance, we can also automatically generate the models of the request
bodies.

> **⚠️** Tip: RESTEasy Reactive supports multipart/form-data [out of the box](https://quarkus.io/guides/rest-client-reactive#multipart). Thus, no additional dependency is required.

If you're using RESTEasy Classic, you need to add the following additional dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-multipart</artifactId>
</dependency>
```

For any multipart/form-data operation a model for the request body will be generated. Each part of the multipart is a field in this model that is annotated with the following annotations:

- `javax.ws.rs.FormParam`, where the value parameter denotes the part name,
- `PartType`, where the parameter is the jax-rs MediaType of the part (see below for details),
- and, if the part contains a file, `PartFilename`, with a generated default parameter that will be passed as the fileName sub-header in the
  Content-Disposition header of the part.

For example, the model for a request that requires a file, a string and some complex object will look like this:

```java
public class MultipartBody {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @PartFilename("defaultFileName")
    public File file;

    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;

    @FormParam("someObject")
    @PartType(MediaType.APPLICATION_JSON)
    public MyComplexObject someObject;
}
```

Then in the client, when using RESTEasy Classic, the `org.jboss.resteasy.annotations.providers.multipart.MultipartForm` annotation is added in front of the multipart parameter:

```java
@Path("/echo")
@RegisterRestClient(baseUri="http://my.endpoint.com/api/v1", configKey="multipart-requests_yml")
public interface MultipartService {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    String sendMultipartData(@MultipartForm MultipartBody data);

}
```

When using RESTEasy Reactive, the `javax.ws.rs.BeanParam` annotation is added in front of the multipart parameter:

```java
@Path("/echo")
@RegisterRestClient(baseUri="http://my.endpoint.com/api/v1", configKey="multipart-requests_yml")
public interface MultipartService {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  String sendMultipartData(@javax.ws.rs.BeanParam MultipartBody data);

}
```

See [Quarkus - Using the REST Client with Multipart](https://quarkus.io/guides/rest-client-multipart) and
the [RESTEasy JAX-RS specifications](https://docs.jboss.org/resteasy/docs/4.7.5.Final/userguide/html_single/index.html) for more details.

> **⚠️** `MultipartForm`  is deprecated when using RESTEasy Reactive.

`baseURI` value of `RegisterRestClient` annotation is extracted from the `servers` section of the file, if present. If not, it will be left empty and it is expected you set up the uri to be used in your configuration.

Importantly, if some multipart request bodies contain complex objects (i.e. non-primitives) you need to explicitly tell the Open API generator to create models for these objects by setting
the `skip-form-model` property corresponding to your spec in the `application.properties` to `false`, e.g.:

```properties
quarkus.openapi-generator.codegen.spec.my_multipart_requests_yml.skip-form-model=false
```

See the module [multipart-request](integration-tests/multipart-request) for an example of how to use this feature.

### Default content-types according to OpenAPI Specification and limitations

The [OAS 3.0](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#special-considerations-for-multipart-content) specifies the following default content-types for a multipart:

- If the property is a primitive, or an array of primitive values, the default Content-Type is `text/plain`
- If the property is complex, or an array of complex values, the default Content-Type is `application/json`
- If the property is a `type: string` with `format: binary` or `format: base64` (aka a file object), the default Content-Type is `application/octet-stream`

A different content-type may be defined in your api spec, but this is not yet supported in the code generation. Also, this "annotation-oriented" approach of RestEasy (i.e. using `@MultipartForm` to
denote the multipart body parameter) does not seem to properly support the unmarshalling of arrays of the same type (e.g. array of files), in these cases it uses Content-Type equal
to `application/json`.

## Generating files via InputStream

Having the files in the `src/main/openapi` directory will generate the REST stubs by default. Alternatively, you can implement
the `io.quarkiverse.openapi.generator.deployment.codegen.OpenApiSpecInputProvider`
interface to provide a list of `InputStream`s of OpenAPI specification files. This is useful in scenarios where you want to dynamically generate the client code without having the target spec file
saved locally in your project.

See the example implementation [here](/integration-tests/generation-input/src/main/java/io/quarkiverse/openapi/generator/codegen/ClassPathPetstoreOpenApiSpecInputProvider.java)

## Skip Deprecated Attributes in Model classes

The domain objects are classes generated in the `model` package. These classes might have [deprecated attributes](https://spec.openapis.org/oas/v3.1.0#fixed-fields-9) in the Open API specification
file. By default, these attributes are generated. You can fine tune this behavior if the deprecated attributes should not be generated.

Use the property key `<base_package>.model.MyClass.generateDeprecated=false` to disable the deprecated attributes in the given model. For example `org.acme.weather.Country.generatedDeprecated=false`.

## Skip Deprecated Operations in API classes

The client objects are classes generated in the `api` package. These classes might have [deprecated operations](https://spec.openapis.org/oas/v3.1.0#operation-object) in the Open API specification
file. By default, these operations are generated. You can fine tune this behavior if the deprecated operations should not be generated.

Use the property key `<base_package>.api.MyClass.generateDeprecated=false` to disable the deprecated operations in the given API. For example `org.acme.openapi.simple.api.DefaultApi.generatedDeprecated=false`.

## Custom Register Providers for generated api

In some cases, we need custom `RegisterProvider` for generated api, e.g. logging. You can define your own Providers in `application.properties` :

| Property Key                                                                                                                                                                        | Example                                                                                                                                |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `quarkus.openapi-generator.codegen.spec.[filename].custom-register-providers` | `quarkus.openapi-generator.codegen.spec.simple_openapi_json.custom-register-providers=org.test.Foo,org.test.Bar`<br/>Provider classes are separated by commas |

With the above configuration, the extension generates your Rest Clients with a code similar to the following:

```java
package org.acme.openapi.simple.api;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("")
@RegisterRestClient(configKey="simple-openapi_json")
@RegisterProvider(org.test.Foo.class)
@RegisterProvider(org.test.Bar.class)
public interface DefaultApi {

    @GET
    @Path("/bye")
    @Produces({ "text/plain" })
    @org.eclipse.microprofile.faulttolerance.CircuitBreaker
    public String byeGet();
}
```

See the module [register-provider](integration-tests/register-provider) for an example of how to use this feature.

## Skip OpenAPI schema validation

Use the property key `quarkus.openapi-generator.codegen.validateSpec=false` to disable validating the input specification file before code generation. By default, invalid specifications will result in an error.

## Type and import mappings

It's possible to remap types in the generated files. For example, instead of a `File` you can configure the code generator to use `InputStream` for all file upload parts of multipart request, or you could change all `UUID` types to `String`. You can configure this in your `application.properties` using the following configuration keys:

| Description    | Property Key                                                                 | Example                                                                                                                                                                        |
|----------------|------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Type Mapping   | `quarkus.openapi-generator.codegen.spec.[filename].type-mappings.[oas_type]` | `quarkus.openapi-generator.codegen.spec.my_spec_yml.type-mappings.File=InputStream` will use `InputStream` as type for all objects of the OAS File type.                       |
| Import Mapping | `quarkus.openapi-generator.codegen.spec.[filename].import-mappings.[type]`   | `quarkus.openapi-generator.codegen.spec.my_spec_yml.import-mappings.File=java.io.InputStream` will replace the default `import java.io.File` with `import java.io.InputStream` |

Note that these configuration properties are maps. For the type-mapping the keys are OAS data types and the values are Java types. 

Another common example is needing `java.time.Instant` as type for date-time fields in your POJO classes. You can achieve with these settings:
```properties
quarkus.openapi-generator.codegen.spec.my_spec_yml.type-mappings.DateTime=Instant
quarkus.openapi-generator.codegen.spec.my_spec_yml.import-mappings.Instant=java.time.Instant
```

It's also possible to only use a type mapping with a fully qualified name, for instance `quarkus.openapi-generator.codegen.spec.my_spec_yml.type-mappings.File=java.io.InputStream`. For more information and a list of all types see the OpenAPI generator documentation on [Type Mappings and Import Mappings](https://openapi-generator.tech/docs/usage/#type-mappings-and-import-mappings). 

See the module [type-mapping](integration-tests/type-mapping) for an example of how to use this feature.

## Known Limitations

These are the known limitations of this pre-release version:

- Only Jackson support

We will work in the next few releases to address these use cases, until there please provide feedback for the current state of this extension. We also love contributions :heart:

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://ricardozanini.medium.com/"><img src="https://avatars.githubusercontent.com/u/1538000?v=4?s=100" width="100px;" alt="Ricardo Zanini"/><br /><sub><b>Ricardo Zanini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ricardozanini" title="Code">💻</a> <a href="#maintenance-ricardozanini" title="Maintenance">🚧</a></td>
      <td align="center"><a href="http://thegreatapi.com"><img src="https://avatars.githubusercontent.com/u/11776454?v=4?s=100" width="100px;" alt="Helber Belmiro"/><br /><sub><b>Helber Belmiro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=hbelmiro" title="Documentation">📖</a> <a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=hbelmiro" title="Code">💻</a></td>
      <td align="center"><a href="http://gastaldi.wordpress.com"><img src="https://avatars.githubusercontent.com/u/54133?v=4?s=100" width="100px;" alt="George Gastaldi"/><br /><sub><b>George Gastaldi</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gastaldi" title="Code">💻</a> <a href="#infra-gastaldi" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center"><a href="https://github.com/RishiKumarRay"><img src="https://avatars.githubusercontent.com/u/87641376?v=4?s=100" width="100px;" alt="Rishi Kumar Ray"/><br /><sub><b>Rishi Kumar Ray</b></sub></a><br /><a href="#infra-RishiKumarRay" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center"><a href="https://github.com/fjtirado"><img src="https://avatars.githubusercontent.com/u/65240126?v=4?s=100" width="100px;" alt="Francisco Javier Tirado Sarti"/><br /><sub><b>Francisco Javier Tirado Sarti</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=fjtirado" title="Code">💻</a></td>
      <td align="center"><a href="https://github.com/Orbifoldt"><img src="https://avatars.githubusercontent.com/u/30009459?v=4?s=100" width="100px;" alt="Orbifoldt"/><br /><sub><b>Orbifoldt</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=Orbifoldt" title="Code">💻</a></td>
      <td align="center"><a href="https://github.com/antssilva96"><img src="https://avatars.githubusercontent.com/u/84567479?v=4?s=100" width="100px;" alt="antssilva96"/><br /><sub><b>antssilva96</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=antssilva96" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/wmedvede"><img src="https://avatars.githubusercontent.com/u/2431454?v=4?s=100" width="100px;" alt="Walter Medvedeo"/><br /><sub><b>Walter Medvedeo</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=wmedvede" title="Code">💻</a></td>
      <td align="center"><a href="https://github.com/miguelchico"><img src="https://avatars.githubusercontent.com/u/6106661?v=4?s=100" width="100px;" alt="Miguel Angel Chico"/><br /><sub><b>Miguel Angel Chico</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=miguelchico" title="Code">💻</a></td>
      <td align="center"><a href="https://github.com/martinweiler"><img src="https://avatars.githubusercontent.com/u/619410?v=4?s=100" width="100px;" alt="Martin Weiler"/><br /><sub><b>Martin Weiler</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=martinweiler" title="Code">💻</a></td>
      <td align="center"><a href="https://leibnizhu.github.io/"><img src="https://avatars.githubusercontent.com/u/13050963?v=4?s=100" width="100px;" alt="Leibniz.Hu"/><br /><sub><b>Leibniz.Hu</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=Leibnizhu" title="Code">💻</a></td>
      <td align="center"><a href="http://melloware.com"><img src="https://avatars.githubusercontent.com/u/4399574?v=4?s=100" width="100px;" alt="Melloware"/><br /><sub><b>Melloware</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=melloware" title="Documentation">📖</a></td>
      <td align="center"><a href="https://github.com/cristianonicolai"><img src="https://avatars.githubusercontent.com/u/570894?v=4?s=100" width="100px;" alt="Cristiano Nicolai"/><br /><sub><b>Cristiano Nicolai</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=cristianonicolai" title="Code">💻</a></td>
      <td align="center"><a href="https://github.com/YassinHajaj"><img src="https://avatars.githubusercontent.com/u/18174180?v=4?s=100" width="100px;" alt="YassinHajaj"/><br /><sub><b>YassinHajaj</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=YassinHajaj" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/gwydionmv"><img src="https://avatars.githubusercontent.com/u/118427625?v=4?s=100" width="100px;" alt="Gwydion Martín"/><br /><sub><b>Gwydion Martín</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gwydionmv" title="Code">💻</a></td>
      <td align="center"><a href="https://www.linkedin.com/in/adrianotagliaferro/"><img src="https://avatars.githubusercontent.com/u/1286247?v=4?s=100" width="100px;" alt="Adriano Augusto Tagliaferro"/><br /><sub><b>Adriano Augusto Tagliaferro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=dritoferro" title="Tests">⚠️</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

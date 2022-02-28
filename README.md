# Quarkus - OpenAPI Generator

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-5-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

Quarkus' extension for generation of [Rest Clients](https://quarkus.io/guides/rest-client) based on OpenAPI specification files.

This extension is based on the [OpenAPI Generator Tool](https://openapi-generator.tech/).

## Getting Started

Add the following dependency to your project's `pom.xml` file:

```xml

<dependency>
  <groupId>io.quarkiverse.openapi.generator</groupId>
  <artifactId>quarkus-openapi-generator</artifactId>
  <version>0.3.0</version>
</dependency>
```

You will also need to add or update the `quarkus-maven-plugin` configuration with the following:

> **⚠️**
You probably already have this configuration if you created your application with the [Quarkus Starter](https://code.quarkus.io/). That said, double-check your configuration not to add another `plugin` entry.

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

To fine tune the configuration for each spec file, add the following entry to your properties file. In this example, our spec file is in `src/main/openapi/petstore.json`:

```properties
quarkus.openapi-generator.codegen.spec."petstore.json".base-package=org.acme.openapi
```

Note that the file name is used to configure the specific information for each spec.

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

## Authentication Support

If your OpenAPI specification file has `securitySchemes` [definitions](https://spec.openapis.org/oas/v3.1.0#security-scheme-object), the inner generator will [register `ClientRequestFilter`s providers](https://download.eclipse.org/microprofile/microprofile-rest-client-2.0/microprofile-rest-client-spec-2.0.html#_provider_declaration) for you to implement the given authentication mechanism.

To provide the credentials for your application, you can use the [Quarkus configuration support](https://quarkus.io/guides/config). The configuration key is composed using this pattern: `[base_package].security.auth.[security_scheme_name]/[auth_property_name]`. Where:

- `base_package` is the package name you gave when configuring the code generation using `quarkus.openapi-generator.codegen.spec.[open_api_file].base-package` property.
- `security_scheme_name` is the name of the [security scheme object definition](https://spec.openapis.org/oas/v3.1.0#security-scheme-object) in the OpenAPI file. Given the following excerpt, we have `api_key` and `basic_auth` security schemes:
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
- `auth_property_name` varies depending on the authentication provider. For example, for Basic Authentication we have `username` and `password`. See the following sections for more details.

> Tip: on production environments you will likely to use [HashiCorp Vault](https://quarkiverse.github.io/quarkiverse-docs/quarkus-vault/dev/index.html) or [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/) to provide this information for your application.

### Basic HTTP Authentication

For Basic HTTP Authentication, these are the supported configurations:

| Description          | Property Key                                                   | Example                                              |
| -------------------- | -------------------------------------------------------------- | ---------------------------------------------------- |
| Username credentials | `[base_package].security.auth.[security_scheme_name]/username` | `org.acme.openapi.security.auth.basic_auth/username` |
| Password credentials | `[base_package].security.auth.[security_scheme_name]/password` | `org.acme.openapi.security.auth.basic_auth/password` |

### Bearer Token Authentication

For Bearer Token Authentication, these are the supported configurations:

| Description  | Property Key                                                       | Example                                                  |
| -------------| ------------------------------------------------------------------ | -------------------------------------------------------- |
| Bearer Token | `[base_package].security.auth.[security_scheme_name]/bearer-token` | `org.acme.openapi.security.auth.bearer/bearer-token` |

### API Key Authentication

Similarly to bearer token, the API Key Authentication also has the token entry key property:

| Description  | Property Key                                                  | Example                                             |
| -------------| --------------------------------------------------------------| --------------------------------------------------- |
| API Key      | `[base_package].security.auth.[security_scheme_name]/api-key` | `org.acme.openapi.security.auth.apikey/api-key` |

The API Key scheme has an additional property that requires where to add the API key in the request token: header, cookie or query. The inner provider takes care of that for you. 

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
quarkus.openapi-generator.codegen.spec."simple-openapi.json".base-package=org.acme.openapi.simple

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
@RegisterRestClient
public interface DefaultApi {

    @GET
    @Path("/bye")
    @Produces({"text/plain"})
    @org.eclipse.microprofile.faulttolerance.CircuitBreaker
    public String byeGet();

    @GET
    @Path("/hello")
    @Produces({"text/plain"})
    public String helloGet();

}
````

You can also override the default Circuit Breaker configuration by setting the properties in `application.properties` [just as you would for a traditional MicroProfile application](https://quarkus.io/guides/smallrye-fault-tolerance#runtime-configuration):

````properties
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/failOn = java.lang.IllegalArgumentException,java.lang.NullPointerException
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/skipOn = java.lang.NumberFormatException
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/delay = 33
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/delayUnit = MILLIS
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/requestVolumeThreshold = 42
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/failureRatio = 3.14
org.acme.openapi.simple.api.DefaultApi/byeGet/CircuitBreaker/successThreshold = 22
````


## Known Limitations

These are the known limitations of this pre-release version:

- No OAuth2 support
- No reactive support
- Only Jackson support

We will work in the next few releases to address these use cases, until there please provide feedback for the current state of this extension. We also love contributions :heart:

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://ricardozanini.medium.com/"><img src="https://avatars.githubusercontent.com/u/1538000?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Ricardo Zanini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=ricardozanini" title="Code">💻</a> <a href="#maintenance-ricardozanini" title="Maintenance">🚧</a></td>
    <td align="center"><a href="http://thegreatapi.com"><img src="https://avatars.githubusercontent.com/u/11776454?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Helber Belmiro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=hbelmiro" title="Documentation">📖</a></td>
    <td align="center"><a href="http://gastaldi.wordpress.com"><img src="https://avatars.githubusercontent.com/u/54133?v=4?s=100" width="100px;" alt=""/><br /><sub><b>George Gastaldi</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=gastaldi" title="Code">💻</a> <a href="#infra-gastaldi" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    <td align="center"><a href="https://github.com/RishiKumarRay"><img src="https://avatars.githubusercontent.com/u/87641376?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Rishi Kumar Ray</b></sub></a><br /><a href="#infra-RishiKumarRay" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    <td align="center"><a href="https://github.com/fjtirado"><img src="https://avatars.githubusercontent.com/u/65240126?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Francisco Javier Tirado Sarti</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-openapi-generator/commits?author=fjtirado" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

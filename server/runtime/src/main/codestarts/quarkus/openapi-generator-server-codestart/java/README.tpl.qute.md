{#include readme-header /}

## Requirements

If you do not have added the `io.quarkus:quarkus-smallrye-openapi` extension in your project, add it first:

### SmallRye OpenAPI:

Quarkus CLI:

```bash
quarkus ext add io.quarkus:quarkus-smallrye-openapi
```

Maven:
```bash
./mvnw quarkus:add-extension -Dextensions="io.quarkus:quarkus-smallrye-openapi"
```

Gradle:

```bash
./gradlew addExtension --extensions="io.quarkus:quarkus-smallrye-openapi"
```
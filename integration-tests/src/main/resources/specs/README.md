# Providing OpenAPI spec files via InputStream

This directory is just meant to be used in the integration test cases. Instead of reading from the `src/main/openapi` directory, a spec file can also be read via a given `InputStream`. 

For this to work, clients must implement the `OpenApiSpecInputProvider` interface. The implementation read the files from any other source (an HTTP server, for example), and provide the input stream.

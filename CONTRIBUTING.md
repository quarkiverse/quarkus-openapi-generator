# Contributing guide

**Want to contribute? Great!** We try to make it easy, and all contributions, even the smaller ones, are more than welcome. This includes bug reports, fixes, documentation, examples... But first, read this page.

## Reporting an issue

This project uses GitHub issues to manage the issues. Open an issue directly in GitHub.

If you believe you found a bug, and it's likely possible, please indicate a way to reproduce it, what you are seeing, and
what you would expect to see. Don't forget to indicate your Quarkus, Java, Maven/Gradle, and GraalVM versions.

## Tests and documentation are not optional

Don't forget to include tests in your pull requests. Also don't forget the documentation (reference documentation, javadoc...).

### RESTEasy Reactive and RESTEasy Classic

This extension supports RESTEasy Reactive and RESTEasy Classic. We have one profile for each implementation. By default, tests are run with RESTEasy Classic.

#### Using the RESTEasy Reactive profile

To run the tests using RESTEasy Reactive, use the `resteasy-reactive` [profile](https://github.com/quarkiverse/quarkus-openapi-generator/blob/32d9bd753724065d7217defb3085a734fea40bc8/integration-tests/pom.xml#L79), like the following:

```shell
mvn verify -Presteasy-reactive
```

#### Using the RESTEasy Classic profile

To run the tests using RESTEasy Classic, use the `resteasy-classic` [profile](https://github.com/quarkiverse/quarkus-openapi-generator/blob/32d9bd753724065d7217defb3085a734fea40bc8/integration-tests/pom.xml#L49), like the following:

```shell
mvn verify -Presteasy-classic
```

#### Specific tests for each implementation

Most of the tests are the same for both RESTEasy implementations, but a few of them require different code, like the `multipart-request` [integration test](https://github.com/quarkiverse/quarkus-openapi-generator/tree/main/integration-tests/multipart-request). For these cases, we have one test for each implementation, using the `org.junit.jupiter.api.Tag` annotation to specify the profile for each of them.

### Code Style

Maven automatically formats code and organizes imports when you run `mvn verify`. So, we recommend you do that before sending your PR. Otherwise, PR checks will fail.


## Quarkus 3 LTS support

We align the `main` branch with the latest Quarkus LTS stream (currently 3.33.x). All features, fixes, and updates target `main`, which provides long-term support aligned with Quarkus' LTS releases.

The `main-lts` branch is archived. No further updates or releases will be made from it. If you're on an older LTS release, we recommend migrating to `main` to get the latest LTS-aligned updates.

The `quarkus2` branch is also archived and no longer maintained.


## For the maintainers

### Backlog

We have a [Kanban board](https://github.com/orgs/quarkiverse/projects/2), which is currently visible only by members of the [Quarkiverse organization](https://github.com/quarkiverse).

### Staling issues and PRs

We have a [GitHub action to automatically close issues and PRs](.github/workflows/stale_issues.yml) that didn't have interactions for a while. If you want to disable it for a specific issue or PR, you can add the `pinned` label and it will never stale.
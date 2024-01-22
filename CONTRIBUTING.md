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

## Quarkus 3 and Quarkus 2 support

We no longer offer support for Quarkus 2. This extension used to support versions 3 and 2 of Quarkus and the code base was different for each Quarkus version. Therefore, we have the `main` branch supporting Quarkus 3, and the `quarkus2` archived branch supporting Quarkus 2. Note that no updates are planned for Quarkus 2 and features and bug fixes are not backported to the `quarkus2` branch.

## For the maintainers

### Backporting between branches

[We have a GitHub action for backporting PRs between different branches](.github/workflows/pr-backporting.yml). To use that, you must set a label named `backport-<destination_branch_name>`.

Let's say you want to backport a PR from the `main` branch to the `quarkus2` branch. You would have to add a label named `backport-quarkus2` to the original PR. When that PR is merged, the GitHub actions bot will send a copy of the PR to the `quarkus2` branch.

See an example:

* [Original PR](https://github.com/quarkiverse/quarkus-openapi-generator/pull/439)
* [Backport PR](https://github.com/quarkiverse/quarkus-openapi-generator/pull/445)

#### Known limitation

GitHub does not initiate checks for pull requests opened by the GitHub Actions bot. Therefore, [when we backport a PR to another branch the PR checks are not run automatically](https://github.com/quarkiverse/quarkus-openapi-generator/issues/450).

### Backlog

We have a [Kanban board](https://github.com/orgs/quarkiverse/projects/2), which is currently visible only by members of the [Quarkiverse organization](https://github.com/quarkiverse).

### Staling issues and PRs

We have a [GitHub action to automatically close issues and PRs](.github/workflows/stale_issues.yml) that didn't have interactions for a while. If you want to disable it for a specific issue or PR, you can add the `pinned` label and it will never stale.
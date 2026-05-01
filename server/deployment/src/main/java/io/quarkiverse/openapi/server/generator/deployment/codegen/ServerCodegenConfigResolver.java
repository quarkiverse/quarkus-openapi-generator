package io.quarkiverse.openapi.server.generator.deployment.codegen;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_DIR;
import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_PACKAGE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.smallrye.config.common.utils.StringUtil;

public class ServerCodegenConfigResolver {

    private static final String SERVER_SPEC_PREFIX = CodegenConfig.getServerSpecPrefix();
    private static final Set<String> SUPPORTED_SPEC_PROPERTIES = Set.of(
            "spec",
            "input-base-dir",
            "base-package",
            "use-reactive",
            "use-builders",
            "use-bean-validation");

    public boolean hasConfiguration(Config config) {
        return getLegacySpec(config).isPresent() || !getConfiguredSpecIds(config).isEmpty();
    }

    public boolean hasConfiguration(Path sourceDir, Config config) {
        if (hasConfiguration(config)) {
            return true;
        }
        Path inputBaseDir = resolveInputBaseDir(sourceDir, Optional.empty(), config);
        if (!Files.isDirectory(inputBaseDir)) {
            return false;
        }
        try (Stream<Path> files = Files.walk(inputBaseDir)) {
            return files.filter(Files::isRegularFile)
                    .anyMatch(ServerCodegenConfigResolver::hasSupportedExtension);
        } catch (IOException e) {
            return false;
        }
    }

    public List<ServerCodegenSpec> resolveSpecs(Path sourceDir, Config config) throws CodeGenException {
        Set<String> configuredSpecIds = getConfiguredSpecIds(config);
        Path inputBaseDir = resolveInputBaseDir(sourceDir, Optional.empty(), config);

        if (!configuredSpecIds.isEmpty()) {
            List<ServerCodegenSpec> specs = new ArrayList<>();
            Set<Path> configuredPaths = new LinkedHashSet<>();
            for (String specId : configuredSpecIds.stream().sorted().toList()) {
                ServerCodegenSpec spec = resolveSpec(sourceDir, config, specId);
                specs.add(spec);
                configuredPaths.add(spec.specPath().normalize());
            }
            specs.addAll(discoverUnconfiguredSpecs(inputBaseDir, config, configuredPaths));
            return specs;
        }

        Optional<String> legacySpec = getLegacySpec(config);
        if (legacySpec.isPresent()) {
            return List.of(resolveLegacySpec(sourceDir, config, legacySpec.get()));
        }

        if (Files.isDirectory(inputBaseDir)) {
            return discoverUnconfiguredSpecs(inputBaseDir, config, Set.of());
        }

        return List.of();
    }

    private List<ServerCodegenSpec> discoverUnconfiguredSpecs(Path inputBaseDir, Config config, Set<Path> configuredPaths)
            throws CodeGenException {
        List<String> filesToInclude = config.getOptionalValues(CodegenConfig.getServerInclude(), String.class)
                .orElse(List.of());
        List<String> filesToExclude = config.getOptionalValues(CodegenConfig.getServerExclude(), String.class)
                .orElse(List.of());

        List<ServerCodegenSpec> specs = new ArrayList<>();
        try (Stream<Path> files = Files.walk(inputBaseDir)) {
            files.filter(Files::isRegularFile)
                    .filter(ServerCodegenConfigResolver::hasSupportedExtension)
                    .filter(path -> !configuredPaths.contains(path.normalize()))
                    .filter(path -> !filesToExclude.contains(path.getFileName().toString()))
                    .filter(path -> filesToInclude.isEmpty() || filesToInclude.contains(path.getFileName().toString()))
                    .sorted(Comparator.naturalOrder())
                    .forEach(path -> {
                        String configKey = sanitize(inputBaseDir.relativize(path));
                        specs.add(new ServerCodegenSpec(
                                configKey,
                                inputBaseDir,
                                path,
                                getValue(config, null, "base-package", String.class).orElse(DEFAULT_PACKAGE),
                                getValue(config, null, "use-reactive", Boolean.class).orElse(false),
                                getValue(config, null, "use-builders", Boolean.class).orElse(true),
                                getValue(config, null, "use-bean-validation", Boolean.class).orElse(false)));
                    });
        } catch (IOException e) {
            throw new CodeGenException("Failed to scan OpenAPI specifications under " + inputBaseDir, e);
        }
        return specs;
    }

    private ServerCodegenSpec resolveLegacySpec(Path sourceDir, Config config, String spec) throws CodeGenException {
        Path inputBaseDir = resolveInputBaseDir(sourceDir, Optional.empty(), config);
        validateOpenApiDir(inputBaseDir);

        return new ServerCodegenSpec(
                sanitize(Path.of(spec)),
                inputBaseDir,
                resolveExplicitSpec(inputBaseDir, spec),
                getValue(config, null, "base-package", String.class).orElse(DEFAULT_PACKAGE),
                getValue(config, null, "use-reactive", Boolean.class).orElse(false),
                getValue(config, null, "use-builders", Boolean.class).orElse(true),
                getValue(config, null, "use-bean-validation", Boolean.class).orElse(false));
    }

    private ServerCodegenSpec resolveSpec(Path sourceDir, Config config, String specId) throws CodeGenException {
        Path inputBaseDir = resolveInputBaseDir(
                sourceDir,
                config.getOptionalValue(getSpecProperty(specId, "input-base-dir"), String.class),
                config);
        validateOpenApiDir(inputBaseDir);

        Optional<String> explicitSpec = config.getOptionalValue(getSpecProperty(specId, "spec"), String.class);
        Path specPath = explicitSpec.isPresent()
                ? resolveExplicitSpec(inputBaseDir, explicitSpec.get())
                : resolveSpecBySanitizedName(inputBaseDir, specId);

        return new ServerCodegenSpec(
                specId,
                inputBaseDir,
                specPath,
                getValue(config, specId, "base-package", String.class).orElse(DEFAULT_PACKAGE),
                getValue(config, specId, "use-reactive", Boolean.class).orElse(false),
                getValue(config, specId, "use-builders", Boolean.class).orElse(true),
                getValue(config, specId, "use-bean-validation", Boolean.class).orElse(false));
    }

    private <T> Optional<T> getValue(Config config, String specId, String propertyName, Class<T> propertyType) {
        Optional<T> specValue = specId == null
                ? Optional.empty()
                : config.getOptionalValue(getSpecProperty(specId, propertyName), propertyType);
        return specValue.or(() -> getLegacyProperty(propertyName)
                .flatMap(property -> config.getOptionalValue(property, propertyType)))
                .or(() -> getServerProperty(propertyName)
                        .flatMap(property -> config.getOptionalValue(property, propertyType)));
    }

    private Set<String> getConfiguredSpecIds(Config config) {
        return StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(propertyName -> propertyName.startsWith(SERVER_SPEC_PREFIX))
                .map(propertyName -> propertyName.substring(SERVER_SPEC_PREFIX.length()))
                .map(propertyName -> {
                    int separator = propertyName.indexOf('.');
                    if (separator < 0) {
                        return null;
                    }
                    String property = propertyName.substring(separator + 1);
                    if (!SUPPORTED_SPEC_PROPERTIES.contains(property)) {
                        return null;
                    }
                    return propertyName.substring(0, separator);
                })
                .filter(specId -> specId != null && !specId.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Optional<String> getLegacySpec(Config config) {
        return config.getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
                .or(() -> config.getOptionalValue(CodegenConfig.getServerSpecPropertyName(), String.class));
    }

    private Path resolveInputBaseDir(Path sourceDir, Optional<String> inputBaseDir, Config config) {
        return inputBaseDir
                .or(() -> config.getOptionalValue(CodegenConfig.getInputBaseDirPropertyName(), String.class))
                .or(() -> config.getOptionalValue(CodegenConfig.getServerInputBaseDirPropertyName(), String.class))
                .map(baseDir -> resolveRelativeToModule(sourceDir, baseDir))
                .orElseGet(() -> sourceDir.resolve(DEFAULT_DIR));
    }

    private static Path resolveRelativeToModule(Path sourceDir, String baseDir) {
        int srcIndex = sourceDir.toString().lastIndexOf("src");
        if (srcIndex < 0) {
            return Path.of(baseDir);
        }
        return Path.of(sourceDir.toString().substring(0, srcIndex), baseDir);
    }

    private static void validateOpenApiDir(Path openApiDir) throws CodeGenException {
        if (!Files.exists(openApiDir)) {
            throw new CodeGenException(
                    "The OpenAPI input base directory does not exist. Please create the directory at " + openApiDir);
        }

        if (!Files.isDirectory(openApiDir)) {
            throw new CodeGenException(
                    "The OpenAPI input base directory is not a directory. Please create the directory at " + openApiDir);
        }
    }

    private static Path resolveExplicitSpec(Path inputBaseDir, String spec) {
        return inputBaseDir.resolve(spec).normalize();
    }

    private static Path resolveSpecBySanitizedName(Path inputBaseDir, String specId) throws CodeGenException {
        try (Stream<Path> files = Files.walk(inputBaseDir)) {
            List<Path> matches = files
                    .filter(Files::isRegularFile)
                    .filter(ServerCodegenConfigResolver::hasSupportedExtension)
                    .filter(path -> sanitize(inputBaseDir.relativize(path)).equals(specId))
                    .sorted(Comparator.naturalOrder())
                    .toList();

            if (matches.isEmpty()) {
                throw new CodeGenException(
                        "No OpenAPI specification matching '" + specId + "' was found under " + inputBaseDir);
            }
            if (matches.size() > 1) {
                throw new CodeGenException(
                        "Multiple OpenAPI specifications matching '" + specId + "' were found under " + inputBaseDir + ": "
                                + matches);
            }

            return matches.get(0);
        } catch (IOException e) {
            throw new CodeGenException("Failed to scan OpenAPI specifications under " + inputBaseDir, e);
        }
    }

    private static boolean hasSupportedExtension(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".json") || fileName.endsWith(".yaml") || fileName.endsWith(".yml");
    }

    public static String sanitize(Path path) {
        return StringUtil.replaceNonAlphanumericByUnderscores(path.toString());
    }

    private static String getSpecProperty(String specId, String propertyName) {
        return SERVER_SPEC_PREFIX + specId + "." + propertyName;
    }

    private static Optional<String> getLegacyProperty(String propertyName) {
        return switch (propertyName) {
            case "base-package" -> Optional.of(CodegenConfig.getBasePackagePropertyName());
            case "use-reactive" -> Optional.of(CodegenConfig.getCodegenReactive());
            case "use-builders" -> Optional.of(CodegenConfig.getGenerateBuilders());
            case "use-bean-validation" -> Optional.of(CodegenConfig.getUseBeanValidation());
            default -> Optional.empty();
        };
    }

    private static Optional<String> getServerProperty(String propertyName) {
        return switch (propertyName) {
            case "base-package" -> Optional.of(CodegenConfig.getServerBasePackagePropertyName());
            case "use-reactive" -> Optional.of(CodegenConfig.getServerCodegenReactive());
            case "use-builders" -> Optional.of(CodegenConfig.getServerGenerateBuilders());
            case "use-bean-validation" -> Optional.of(CodegenConfig.getServerUseBeanValidation());
            default -> Optional.empty();
        };
    }
}

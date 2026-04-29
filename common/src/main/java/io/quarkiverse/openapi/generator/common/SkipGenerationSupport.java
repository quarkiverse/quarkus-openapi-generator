package io.quarkiverse.openapi.generator.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;

import io.quarkus.builder.Version;

public class SkipGenerationSupport {
    public String computeFingerprint(OpenApiGeneratorOptions options) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            updateDigestWithFile(digest, options.openApiFilePath());
            updateDigestWithString(digest, "reactive=" + options.isRestEasyReactive());
            updateDigestWithString(digest, "generator=" + getClass().getName());
            updateDigestWithString(digest, "quarkusVersion=" + Version.getVersion());

            addRelevantConfig(digest, options.config(), options.codegenConfigPrefix());

            Path templateDir = options.templateDir();
            if (templateDir != null && Files.isDirectory(templateDir)) {
                try (Stream<Path> paths = Files.walk(templateDir).sorted()) {
                    for (Path path : paths.filter(Files::isRegularFile).toList()) {
                        updateDigestWithString(digest, "template:" + templateDir.relativize(path));
                        updateDigestWithFile(digest, path);
                    }
                }
            }

            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute fingerprint for " + options.openApiFilePath(), e);
        }
    }

    public boolean shouldSkipGeneration(OpenApiGeneratorOptions options, String fingerprint) {
        Path checksumFile = resolveChecksumFile(options.outDir(), options.sanitizedFileName());

        if (!Files.exists(checksumFile)) {
            return false;
        }

        if (!hasGeneratedFiles(options)) {
            return false;
        }

        try {
            String previous = Files.readString(checksumFile);
            return previous.equals(fingerprint);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Store a checksum based on spec and configuration,
     * in order to avoid regenerating code when the latter hasn't changed
     */
    public void persistFingerprint(OpenApiGeneratorOptions options, String fingerprint) {
        Path checksumFile = resolveChecksumFile(options.outDir(), options.sanitizedFileName());

        try {
            Files.createDirectories(checksumFile.getParent());
            Files.writeString(checksumFile, fingerprint);
        } catch (IOException e) {
            String message = "Unable to persist OpenAPI generation fingerprint for " + options.openApiFilePath();
            throw new RuntimeException(message, e);
        }
    }

    private Path resolveChecksumFile(Path outDir, String sanitizedFileName) {
        return outDir.resolve(".quarkus-openapi-generator")
                .resolve(sanitizedFileName + ".sha256");
    }

    private void updateDigestWithString(MessageDigest digest, String value) {
        digest.update(value.getBytes(StandardCharsets.UTF_8));
    }

    private void updateDigestWithFile(MessageDigest digest, Path file) throws IOException {
        digest.update(Files.readAllBytes(file));
    }

    // Includes configuration in the fingerprint so changes force regeneration
    private void addRelevantConfig(MessageDigest digest, Config config, String codegenConfigPrefix) {
        String prefix = "quarkus." + codegenConfigPrefix + ".";

        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(propertyName -> propertyName.startsWith(prefix))
                .sorted()
                .forEach(propertyName -> config.getOptionalValue(propertyName, String.class)
                        .ifPresent(value -> updateDigestWithString(digest, propertyName + "=" + value)));
    }

    private boolean hasGeneratedFiles(OpenApiGeneratorOptions options) {
        Path outDir = options.outDir();
        if (!Files.isDirectory(outDir)) {
            return false;
        }

        try (Stream<Path> paths = Files.walk(outDir)) {
            return paths.anyMatch(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"));
        } catch (IOException e) {
            return false;
        }
    }
}

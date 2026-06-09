package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenSpec;

class ApicurioCodegenWrapperTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExtractRegularEntryInsideOutputDir() throws Exception {
        Path zipPath = tempDir.resolve("safe.zip");
        createZip(zipPath, "nested/proof.txt", "ok");

        Path outputDir = tempDir.resolve("out");
        Files.createDirectories(outputDir);

        invokeUnzip(zipPath.toFile(), outputDir.toFile());

        Path extractedFile = outputDir.resolve("nested/proof.txt");
        assertTrue(Files.exists(extractedFile));
        assertEquals("ok", Files.readString(extractedFile));
    }

    @Test
    void shouldRejectPathTraversalEntry() throws Exception {
        Path zipPath = tempDir.resolve("traversal.zip");
        createZip(zipPath, "../../proof.txt", "pwned");

        Path outputDir = tempDir.resolve("out");
        Files.createDirectories(outputDir);

        IOException error = assertThrows(IOException.class, () -> invokeUnzip(zipPath.toFile(), outputDir.toFile()));
        assertTrue(error.getMessage().contains("Invalid ZIP entry"));
    }

    private static void createZip(Path zipPath, String entryName, String content) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            zipOutputStream.write(content.getBytes());
            zipOutputStream.closeEntry();
        }
    }

    private static void invokeUnzip(File zipFile, File outputDir) throws Exception {
        Method unzipMethod = ApicurioCodegenWrapper.class.getDeclaredMethod("unzip", File.class, File.class);
        unzipMethod.setAccessible(true);

        ApicurioCodegenWrapper wrapper = new ApicurioCodegenWrapper(outputDir,
                new ServerCodegenSpec("test", outputDir.toPath(), outputDir.toPath(), "org.acme", false, false, false));

        try {
            unzipMethod.invoke(wrapper, zipFile, outputDir);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException(cause);
        }
    }
}

package io.quarkiverse.openapi.server.generator.deployment.codegen;

import static io.quarkiverse.openapi.server.generator.deployment.CodegenConfig.getBasePackagePropertyName;

import java.io.*;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.apicurio.hub.api.codegen.JaxRsProjectSettings;
import io.apicurio.hub.api.codegen.OpenApi2JaxRs;
import io.quarkus.bootstrap.prebuild.CodeGenException;

public class ApicurioCodegenWrapper {

    private static final Logger log = LoggerFactory.getLogger(ApicurioOpenApiServerCodegen.class);
    private static final String DEFAULT_PACKAGE = "io.apicurio.api";

    private final Config config;
    private final File outdir;
    private final JaxRsProjectSettings projectSettings;

    public ApicurioCodegenWrapper(Config config, File outdir) {
        this(config, outdir, defaultProjectSettings());
    }

    public ApicurioCodegenWrapper(Config config, File outdir, JaxRsProjectSettings projectSettings) {
        this.config = config;
        this.outdir = outdir;
        this.projectSettings = projectSettings;
        this.projectSettings.setJavaPackage(getBasePackage(config));
    }

    public void generate(Path openApiResource) throws CodeGenException {
        final File openApiFile = openApiResource.toFile();

        log.info("Generating JAX-RS interfaces and beans from: " + openApiResource);

        if (outdir.isFile()) {
            throw new CodeGenException(
                    "Output directory is unexpectedly a file (should be a directory or non-existent).");
        }

        if (!outdir.exists()) {
            outdir.mkdirs();
        }

        // Generate code - output a ZIP file.
        File zipFile = new File(outdir, "generated-code.zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            OpenApi2JaxRs generator = new OpenApi2JaxRs();
            generator.setSettings(projectSettings);
            generator.setUpdateOnly(true);
            generator.setOpenApiDocument(new FileInputStream(openApiFile));
            log.info("Generating code...");
            generator.generate(fos);
        } catch (Exception e) {
            log.error("Error generating code from openapi spec", e);
            throw new CodeGenException(e);
        }

        // Unpack the temporary ZIP file
        log.info("Code generated, unpacking the output ZIP.");
        try {
            unzip(zipFile, outdir);
        } catch (IOException e) {
            log.error("Error generating code from openapi spec", e);
            throw new CodeGenException(e);
        } finally {
            // Delete the temporary ZIP file
            zipFile.delete();
        }

        log.info("Code successfully generated.");
    }

    private void unzip(File fromZipFile, File toOutputDir) throws IOException {
        try (java.util.zip.ZipFile zipFile = new ZipFile(fromZipFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(toOutputDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                            OutputStream out = new FileOutputStream(entryDestination)) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
    }

    private String getBasePackage(final Config config) {
        return config
                .getOptionalValue(getBasePackagePropertyName(), String.class)
                .orElse(DEFAULT_PACKAGE);
    }

    private static JaxRsProjectSettings defaultProjectSettings() {
        JaxRsProjectSettings projectSettings = new JaxRsProjectSettings();
        projectSettings.setJavaPackage(DEFAULT_PACKAGE);

        projectSettings.setReactive(false);
        projectSettings.setCodeOnly(true);
        projectSettings.setCliGenCI(false);
        projectSettings.setMavenFileStructure(false);
        projectSettings.setIncludeSpec(false);
        projectSettings.setCliGenCI(false);
        return projectSettings;
    }
}

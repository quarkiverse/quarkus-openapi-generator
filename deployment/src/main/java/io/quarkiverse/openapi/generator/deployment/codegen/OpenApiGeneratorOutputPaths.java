package io.quarkiverse.openapi.generator.deployment.codegen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OpenApiGeneratorOutputPaths {

    public static final String YAML_PATH = "open-api-yaml";
    public static final String YML_PATH = "open-api-yml";
    public static final String JSON_PATH = "open-api-json";
    public static final String STREAM_PATH = "open-api-stream";

    private static final Collection<String> rootPaths = Arrays.asList(STREAM_PATH);

    public static Path getRelativePath(Path path) {
        List<String> paths = new ArrayList<>();
        Path currentPath = path;
        while (currentPath != null && currentPath.getFileName() != null) {
            if (rootPaths.contains(currentPath.getFileName().toString())) {
                Iterator<String> iter = paths.iterator();
                Path result = Path.of(iter.next());
                while (iter.hasNext()) {
                    result = result.resolve(iter.next());
                }
                return result;
            } else {
                paths.add(0, currentPath.getFileName().toString());
                currentPath = currentPath.getParent();
            }
        }
        return path.getFileName();
    }
}

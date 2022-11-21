package io.quarkiverse.spec.generator.deployment.codegen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpecApiGeneratorOutputPaths {

    private SpecApiGeneratorOutputPaths() {
    }

    private static final String STREAM_SUFFIX = "-" + SpecApiConstants.STREAM;

    public static Path getRelativePath(Path path) {
        List<String> paths = new ArrayList<>();
        Path currentPath = path;
        while (currentPath != null && currentPath.getFileName() != null) {
            if (currentPath.getFileName().toString().endsWith(STREAM_SUFFIX)) {
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

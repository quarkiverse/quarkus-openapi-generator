package io.quarkiverse.openapi.moqu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {

    public static String readContentFromFile(String resourcePath) {
        URL url = Thread.currentThread().getContextClassLoader().getResource((resourcePath));
        assert url != null;
        try {
            return Files.readString(Path.of(url.toURI()));
        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }
}

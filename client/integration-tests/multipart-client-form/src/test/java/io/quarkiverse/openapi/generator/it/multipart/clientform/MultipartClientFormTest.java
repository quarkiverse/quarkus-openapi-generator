package io.quarkiverse.openapi.generator.it.multipart.clientform;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

import org.acme.openapi.multipart.clientform.api.MultipartApi;
import org.acme.openapi.multipart.clientform.model.Metadata;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.client.api.ClientMultipartForm;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockMultipartClientForm.class)
@Tag("resteasy-reactive")
public class MultipartClientFormTest {

    WireMockServer multipartServer;

    @RestClient
    @Inject
    MultipartApi multipartApi;

    @Test
    public void testClientMultipartFormGeneration(@TempDir Path tempDir) throws IOException {

        File testFile = File.createTempFile("test", ".txt", tempDir.toFile());
        Files.writeString(testFile.toPath(), "Test file content");

        Metadata metadata = new Metadata()
                .author("John Doe")
                .tags(List.of("test", "document"));

        ClientMultipartForm form = ClientMultipartForm.create()
                // The RESTEasy Reactive client encoder reads files from disk, so pass the file path here.
                .binaryFileUpload("file", testFile.getName(), testFile.getAbsolutePath(),
                        MediaType.APPLICATION_OCTET_STREAM)
                .attribute("fileName", "test-file.txt", MediaType.TEXT_PLAIN)
                .entity("metadata", metadata, MediaType.APPLICATION_JSON, Metadata.class);

        Map<String, String> response = multipartApi.sendMultipartData(form);

        assertNotNull(response);
        assertEquals("success", response.get("status"));
        assertEquals("File uploaded", response.get("message"));

        multipartServer.verify(postRequestedFor(urlEqualTo("/multipart"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("file")
                        .withHeader("Content-Disposition", containing("filename="))
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_OCTET_STREAM))
                        .withBody(equalTo("Test file content")).build()));

        multipartServer.verify(postRequestedFor(urlEqualTo("/multipart"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("fileName")
                        .withHeader(ContentTypeHeader.KEY, containing(MediaType.TEXT_PLAIN))
                        .withBody(equalTo("test-file.txt")).build()));

        multipartServer.verify(postRequestedFor(urlEqualTo("/multipart"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("metadata")
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_JSON))
                        .withBody(matchingJsonPath("$.author", equalTo("John Doe")))
                        .withBody(matchingJsonPath("$.tags[0]", equalTo("test")))
                        .withBody(matchingJsonPath("$.tags[1]", equalTo("document"))).build()));
    }

    @Test
    public void testSimpleMultipartForm() {

        ClientMultipartForm form = ClientMultipartForm.create()
                .attribute("name", "Alice", MediaType.TEXT_PLAIN)
                .attribute("age", "30", MediaType.TEXT_PLAIN);

        multipartApi.sendSimpleMultipart(form);

        multipartServer.verify(postRequestedFor(urlEqualTo("/simple-multipart"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("name")
                        .withBody(equalTo("Alice")).build()));

        multipartServer.verify(postRequestedFor(urlEqualTo("/simple-multipart"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("age")
                        .withBody(equalTo("30")).build()));
    }

    @Test
    public void testGeneratedApiUsesClientMultipartForm() throws NoSuchMethodException {

        var method = MultipartApi.class.getMethod("sendMultipartData", ClientMultipartForm.class);
        assertNotNull(method, "Method sendMultipartData should exist");
        assertEquals(ClientMultipartForm.class, method.getParameterTypes()[0],
                "Method should accept ClientMultipartForm as parameter");

        var simpleMethod = MultipartApi.class.getMethod("sendSimpleMultipart", ClientMultipartForm.class);
        assertNotNull(simpleMethod, "Method sendSimpleMultipart should exist");
        assertEquals(ClientMultipartForm.class, simpleMethod.getParameterTypes()[0],
                "Method should accept ClientMultipartForm as parameter");
    }
}

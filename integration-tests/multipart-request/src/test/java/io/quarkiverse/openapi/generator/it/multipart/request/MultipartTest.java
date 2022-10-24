package io.quarkiverse.openapi.generator.it.multipart.request;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.acme.openapi.multipart.api.UserProfileDataApi;
import org.acme.openapi.multipart.api.UserProfileDataApi.PostUserProfileDataMultipartForm;
import org.acme.openapi.multipart.model.Address;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockMultipart.class)
public class MultipartTest {

    WireMockServer multipartServer;

    @RestClient
    @Inject
    UserProfileDataApi userProfileDataApi;

    @Test
    public void testUploadMultipartFormdata(@TempDir Path tempDir) throws IOException {
        File testFile = File.createTempFile("test", "", tempDir.toFile());
        try (PrintWriter printWriter = new PrintWriter(testFile)) {
            printWriter.print("Content of the file");
        }

        PostUserProfileDataMultipartForm requestBody = new PostUserProfileDataMultipartForm();
        requestBody.address = new Address().street("Champs-Elysees").city("Paris");
        requestBody.id = UUID.fromString("00112233-4455-6677-8899-aabbccddeeff");
        requestBody.profileImage = testFile;

        userProfileDataApi.postUserProfileData(requestBody);

        multipartServer.verify(postRequestedFor(urlEqualTo("/user-profile-data"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("id")
                        // Primitive => text/plain
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.TEXT_PLAIN))
                        .withBody(equalTo("00112233-4455-6677-8899-aabbccddeeff")).build())
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("address")
                        // Complex value => application/json
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_JSON))
                        .withBody(equalToJson("{\"street\":\"Champs-Elysees\", \"city\":\"Paris\"}")).build())
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("profileImage")
                        .withHeader("Content-Disposition", containing("filename="))
                        // binary string => application/octet-stream
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_OCTET_STREAM))
                        .withBody(equalTo("Content of the file")).build()));
    }

    @Test
    public void testUploadBase64EncodedFile() {
        String sentData = "Some data that's sent!";
        String base64Data = Base64.getEncoder().encodeToString(sentData.getBytes(StandardCharsets.UTF_8));
        UserProfileDataApi.PostBase64DataMultipartForm requestBody = new UserProfileDataApi.PostBase64DataMultipartForm();
        requestBody.encodedFile = base64Data;

        userProfileDataApi.postBase64Data(requestBody);

        multipartServer.verify(postRequestedFor(urlEqualTo("/base64"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("file")
                        .withHeader("Content-Disposition", containing("filename="))
                        // base64 string => application/octet-stream
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_OCTET_STREAM))
                        .withBody(equalTo(base64Data)).build()));
    }
}

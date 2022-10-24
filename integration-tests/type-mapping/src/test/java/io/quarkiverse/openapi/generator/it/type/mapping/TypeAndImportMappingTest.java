package io.quarkiverse.openapi.generator.it.type.mapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.acme.openapi.typemapping.api.TypeMappingApi;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockTypeAndImportMapping.class)
public class TypeAndImportMappingTest {

    WireMockServer typeMappingServer;

    @RestClient
    @Inject
    TypeMappingApi typeMappingApi;

    @Test
    public void canMapTypesAndImportToDifferentValues() {
        final String testUuid = "00112233-4455-6677-8899-aabbccddeeff";
        final InputStream testFile = new ByteArrayInputStream("Content of the file".getBytes(StandardCharsets.UTF_8));

        TypeMappingApi.PostTheDataMultipartForm requestBody = new TypeMappingApi.PostTheDataMultipartForm();
        requestBody.id = testUuid; // String instead of UUID
        requestBody.binaryStringFile = testFile; // InputStream instead of File
        // dateTime remains OffsetDateTime (as is default)
        requestBody.dateTime = OffsetDateTime.of(2000, 2, 13, 4, 5, 6, 0, ZoneOffset.UTC);

        typeMappingApi.postTheData(requestBody);

        typeMappingServer.verify(postRequestedFor(urlEqualTo("/type-mapping"))
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("id")
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.TEXT_PLAIN))
                        .withBody(equalTo(testUuid)).build())
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("dateTime")
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.TEXT_PLAIN))
                        .withBody(equalTo("2000-02-13T04:05:06Z")).build())
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("binaryStringFile")
                        .withHeader("Content-Disposition", containing("filename="))
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_OCTET_STREAM))
                        .withBody(equalTo("Content of the file")).build()));
    }
}

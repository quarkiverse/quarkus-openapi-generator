package io.quarkiverse.openapi.generator.it.type.mapping;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

import org.acme.openapi.typemapping.api.TypeMappingApi;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockTypeAndImportMapping.class)
@Tag("resteasy-classic")
class TypeAndImportMappingRestEasyClassicTest {

    WireMockServer typeMappingServer;

    @RestClient
    @Inject
    TypeMappingApi typeMappingApi;

    @Test
    public void canMapTypesAndImportToDifferentValues() {
        final String testUuid = "00112233-4455-6677-8899-aabbccddeeff";
        final InputStream testFile = new ByteArrayInputStream("Content of the file".getBytes(StandardCharsets.UTF_8));
        final YearMonth testYearMonth = YearMonth.parse("2024-06");

        TypeMappingApi.PostTheDataMultipartForm requestBody = new TypeMappingApi.PostTheDataMultipartForm();
        requestBody.id = testUuid; // String instead of UUID
        requestBody.binaryStringFile = testFile; // InputStream instead of File
        requestBody.yearMonth = testYearMonth; // YearMonth instead of String
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
                        .withBody(equalTo("Content of the file")).build())
                .withRequestBodyPart(new MultipartValuePatternBuilder()
                        .withName("yearMonth")
                        .withHeader(ContentTypeHeader.KEY, equalTo(MediaType.APPLICATION_JSON))
                        .withBody(equalTo("\"2024-06\"")).build()));
    }
}

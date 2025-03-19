package io.quarkiverse.openapi.generator.devui;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.openapi.generator.items.MoquBuildItem;
import io.quarkiverse.openapi.generator.moqu.recorder.MoquRoutesRecorder;
import io.quarkiverse.openapi.moqu.marshall.ObjectMapperFactory;
import io.quarkiverse.openapi.moqu.wiremock.mapper.WiremockMapper;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockMapping;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;

public class MoquWiremockDevUIProcessor {

    private static final String MAPPINGS_KEY = "mappings";
    private static final String WIREMOCK_MAPPINGS_JSON = "/wiremock-mappings.json";

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void generateWiremock(List<MoquBuildItem> mocks, NonApplicationRootPathBuildItem nonApplicationRootPath,
            BuildProducer<RouteBuildItem> routes,
            MoquRoutesRecorder recorder) {

        WiremockMapper wiremockMapper = new WiremockMapper();
        ObjectMapper objMapper = ObjectMapperFactory.getInstance();

        for (MoquBuildItem mock : mocks) {
            List<WiremockMapping> wiremockMappings = wiremockMapper.map(mock.getMoqu());
            try {
                String json = objMapper.writeValueAsString(Map.of(
                        MAPPINGS_KEY, wiremockMappings));

                String uri = mock.prefixUri(nonApplicationRootPath.resolvePath("moqu"))
                        .concat(WIREMOCK_MAPPINGS_JSON);

                routes.produce(nonApplicationRootPath.routeBuilder()
                        .routeFunction(uri, recorder.handleFile(json))
                        .displayOnNotFoundPage()
                        .build());

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @BuildStep(onlyIf = { IsDevelopment.class })
    CardPageBuildItem cardPageBuildItem(
            List<MoquBuildItem> moquMocks,
            NonApplicationRootPathBuildItem nonApplicationRootPath) {
        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        List<MoquModel> models = moquMocks.stream()
                .map(m -> new MoquModel(m.getFullFilename(), m.prefixUri(
                        nonApplicationRootPath.resolvePath("moqu"))
                        .concat("/wiremock-mappings.json")))
                .toList();

        cardPageBuildItem.addBuildTimeData("mocks", models);

        cardPageBuildItem.addPage(
                Page.webComponentPageBuilder()
                        .title("Moqu Wiremock")
                        .icon("font-awesome-solid:server")
                        .componentLink("qwc-moqu.js")
                        .staticLabel(String.valueOf(moquMocks.size())));

        return cardPageBuildItem;
    }
}

package io.quarkiverse.openapi.moqu.wiremock.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkiverse.openapi.moqu.Moqu;
import io.quarkiverse.openapi.moqu.MoquMapper;
import io.quarkiverse.openapi.moqu.model.Request;
import io.quarkiverse.openapi.moqu.model.RequestResponsePair;
import io.quarkiverse.openapi.moqu.model.Response;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockMapping;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockRequest;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockResponse;

public class WiremockMapper implements MoquMapper<WiremockMapping> {

    @Override
    public List<WiremockMapping> map(Moqu moqu) {
        ArrayList<WiremockMapping> definitions = new ArrayList<>();
        for (RequestResponsePair pair : moqu.getRequestResponsePairs()) {

            Request mockRequest = pair.request();
            Response mockResponse = pair.response();

            WiremockRequest request = new WiremockRequest(mockRequest.httpMethod(), mockRequest.url());

            Map<String, Object> headers = new HashMap<>();

            mockResponse.headers().forEach(item -> headers.put(item.name(), item.value()));

            WiremockResponse response = new WiremockResponse(mockResponse.statusCode(), mockResponse.content(), headers);

            definitions.add(new WiremockMapping(
                    request, response));
        }

        return definitions;
    }
}

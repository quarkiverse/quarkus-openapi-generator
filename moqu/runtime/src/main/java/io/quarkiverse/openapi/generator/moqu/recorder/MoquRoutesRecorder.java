package io.quarkiverse.openapi.generator.moqu.recorder;

import java.util.function.Consumer;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class MoquRoutesRecorder {

    public Consumer<Route> handleFile(String content) {
        return new Consumer<Route>() {
            @Override
            public void accept(Route route) {
                route.method(HttpMethod.GET);
                route.handler(new Handler<RoutingContext>() {
                    @Override
                    public void handle(RoutingContext routingContext) {
                        HttpServerResponse response = routingContext.response();
                        HttpServerRequest request = routingContext.request();

                        String mode = request.getParam("mode");
                        if (mode != null && mode.equalsIgnoreCase("see")) {
                            response.putHeader("Content-Type", "application/json; charset=utf-8");
                            response.end(Buffer.buffer(content));
                        } else {
                            setForDownloading(response, content);
                        }
                    }

                });
            }
        };
    }

    private void setForDownloading(HttpServerResponse response, String content) {
        response.putHeader("Content-Type", "application/octet-stream");
        response.putHeader("Content-Disposition", "attachment; filename=wiremock-mappings.json");
        response.end(Buffer.buffer(content));
    }
}

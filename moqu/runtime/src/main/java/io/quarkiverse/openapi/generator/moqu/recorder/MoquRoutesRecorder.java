package io.quarkiverse.openapi.generator.moqu.recorder;

import java.util.function.Consumer;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class MoquRoutesRecorder {

    public Consumer<Route> handleFile(String spec) {
        return new Consumer<Route>() {
            @Override
            public void accept(Route route) {
                route.method(HttpMethod.GET);
                route.handler(new Handler<RoutingContext>() {
                    @Override
                    public void handle(RoutingContext routingContext) {
                        routingContext.response().headers()
                                .add("Content-Type", "plain/text");
                        routingContext.response().send(spec);
                    }
                });
            }
        };
    }
}

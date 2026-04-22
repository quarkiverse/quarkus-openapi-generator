package org.acme.resources;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.acme.model.Model;
import org.jboss.resteasy.reactive.RestResponse;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Path("")
public class DefaultResourceImpl implements DefaultResource {

    @Override
    public Uni<RestResponse<List<Model>>> callList() {
        return Uni.createFrom().item(RestResponse.ok(List.of(new Model().id("1"))));
    }

    @Override
    public Uni<RestResponse<Model>> create() {
        return Uni.createFrom().item(
                RestResponse.status(Response.Status.CREATED, new Model().id("1")));
    }

    @Override
    public Uni<RestResponse<Void>> forbidden() {
        return Uni.createFrom().item(RestResponse.status(Response.Status.NO_CONTENT));
    }

    @Override
    public Uni<RestResponse<String>> hello() {
        return Uni.createFrom().item(RestResponse.ok("Hello, World!"));
    }

    @Override
    public Uni<RestResponse<Map<String, Model>>> map() {
        return Uni.createFrom().item(RestResponse.ok(Map.of("key", new Model().id("1"))));
    }
}

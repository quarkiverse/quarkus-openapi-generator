package org.acme.resources;

import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.acme.model.Model;
import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
@Path("")
public class DefaultResourceImpl implements DefaultResource {

    @Override
    public RestResponse<List<Model>> callList() {
        return RestResponse.ok(List.of(new Model().id("1")));
    }

    @Override
    public RestResponse<Model> create() {
        return RestResponse.status(Response.Status.CREATED, new Model().id("1"));
    }

    @Override
    public RestResponse<Void> forbidden() {
        return RestResponse.status(Response.Status.NO_CONTENT);
    }

    @Override
    public RestResponse<String> hello() {
        return RestResponse.ok("Hello, World!");
    }

    @Override
    public RestResponse<Map<String, Model>> map() {
        return RestResponse.ok(Map.of("key", new Model().id("1")));
    }
}

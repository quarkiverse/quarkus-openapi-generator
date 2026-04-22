package org.acme.resources;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
@Path("")
public class DefaultResourceImpl implements DefaultResource {

    @Override
    public RestResponse<String> returnTypeExt() {
        return RestResponse.ok("return-type");
    }

    @Override
    public RestResponse<String> special() {
        return RestResponse.ok("special");
    }

    @Override
    public String standard() {
        return "standard";
    }
}

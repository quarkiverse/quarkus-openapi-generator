package org.acme;

import jakarta.ws.rs.core.Response;

public class UnconfiguredResourceImpl implements UnconfiguredResource {

    @Override
    public Response listUnconfigured() {
        return Response.ok().build();
    }
}

package io.quarkiverse.openapi.generator.it.refresh;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/protected")
public class ProtectedServiceResource {

    @RestClient
    org.acme.refresh.api.DefaultApi refreshApi;

    @RestClient
    org.acme.norefresh.api.DefaultApi noRefreshApi;

    @POST
    @Path("refresh")
    public Response callRefresh() {
        return call(refreshApi::callProtected);
    }

    @POST
    @Path("no-refresh")
    public Response callNoRefresh() {
        return call(noRefreshApi::callProtected);
    }

    private Response call(Runnable invocation) {
        try {
            invocation.run();
            return Response.ok().build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}

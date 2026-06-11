package io.quarkiverse.openapi.generator.it.pathencoding;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class PathEncodingResource {

    @GET
    @Path("/users/{email:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@PathParam("email") String email) {
        String response = String.format(
                "{\"email\":\"%s\",\"name\":\"Test User\",\"id\":12345}",
                email);
        return Response.ok(response).build();
    }

    @GET
    @Path("/resources/{resourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResourceById(@PathParam("resourceId") String resourceId) {
        String response = String.format(
                "{\"id\":\"%s\",\"name\":\"Resource\"}",
                resourceId);
        return Response.ok(response).build();
    }
}

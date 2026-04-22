package io.quarkiverse.openapi.generator.it.multisegment;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class MultiSegmentResource {

    private static final String EXPECTED_TOKEN = "Bearer test-token-123";

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }

    @GET
    @Path("/repos/{owner}/{ref:.+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepoRef(
            @PathParam("owner") String owner,
            @PathParam("ref") String ref,
            @HeaderParam("Authorization") String authHeader) {

        // Verify authentication
        if (authHeader == null || !authHeader.equals(EXPECTED_TOKEN)) {
            return Response.status(401).entity("{\"message\":\"Unauthorized\"}").build();
        }

        // Return mock response
        String response = String.format(
                "{\"ref\":\"refs/%s\",\"url\":\"https://api.github.com/repos/%s/git/refs/%s\",\"object\":{\"type\":\"commit\",\"sha\":\"abc123\",\"url\":\"https://api.github.com/repos/%s/git/commits/abc123\"}}",
                ref, owner, ref, owner);

        return Response.ok(response).build();
    }

    @GET
    @Path("/users/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(
            @PathParam("username") String username,
            @HeaderParam("Authorization") String authHeader) {

        // Verify authentication
        if (authHeader == null || !authHeader.equals(EXPECTED_TOKEN)) {
            return Response.status(401).entity("{\"message\":\"Unauthorized\"}").build();
        }

        // Return mock response
        String response = String.format(
                "{\"login\":\"%s\",\"id\":12345,\"url\":\"https://api.github.com/users/%s\"}",
                username, username);

        return Response.ok(response).build();
    }
}

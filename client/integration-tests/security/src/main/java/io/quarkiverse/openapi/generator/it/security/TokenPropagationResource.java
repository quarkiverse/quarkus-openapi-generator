package io.quarkiverse.openapi.generator.it.security;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/token_propagation")
public class TokenPropagationResource {

    @RestClient
    org.acme.externalservice1.api.DefaultApi defaultApi1;

    @RestClient
    org.acme.externalservice2.api.DefaultApi defaultApi2;

    @RestClient
    org.acme.externalservice3.api.DefaultApi defaultApi3;

    @RestClient
    org.acme.externalservice4.api.DefaultApi defaultApi4;

    @RestClient
    org.acme.externalservice5.api.DefaultApi defaultApi5;

    @RestClient
    org.acme.externalservice6.api.DefaultApi defaultApi6;

    @RestClient
    org.acme.externalservice7.api.DefaultApi defaultApi7;

    @POST
    @Path("service1")
    public Response service1() {
        return defaultApi1.executeQuery1();
    }

    @POST
    @Path("service2")
    public Response service2() {
        return defaultApi2.executeQuery2();
    }

    @POST
    @Path("service3")
    public Response service3() {
        return defaultApi3.executeQuery3();
    }

    @POST
    @Path("service4")
    public Response service4() {
        return defaultApi4.executeQuery4();
    }

    @POST
    @Path("service5")
    public Response service5() {
        return defaultApi5.executeQuery5();
    }

    @POST
    @Path("service6")
    public Response service6() {
        return defaultApi6.executeQuery6();
    }

    @POST
    @Path("service7")
    public Response service7() {
        return defaultApi7.executeQuery7();
    }
}

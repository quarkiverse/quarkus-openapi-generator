package io.quarkiverse.openapi.generator.it.auth;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/token_server")
public class TokenServerResource {

    @RestClient
    org.acme.externalservice1.api.DefaultApi defaultApi1;

    @RestClient
    org.acme.externalservice2.api.DefaultApi defaultApi2;

    @RestClient
    org.acme.externalservice3.api.DefaultApi defaultApi3;

    @RestClient
    org.acme.externalservice5.api.DefaultApi defaultApi5;

    @RestClient
    org.acme.externalservice6.api.DefaultApi defaultApi6;

    @POST
    @Path("service1")
    public String service1() {
        defaultApi1.executeQuery1();
        return "hello";
    }

    @POST
    @Path("service2")
    public String service2() {
        defaultApi2.executeQuery2();
        return "hello";
    }

    @POST
    @Path("service3")
    public String service3() {
        defaultApi3.executeQuery3();
        return "hello";
    }

    @POST
    @Path("service5")
    public String service5() {
        defaultApi5.executeQuery5();
        return "hello";
    }

    @POST
    @Path("service6")
    public String service6() {
        defaultApi6.executeQuery6();
        return "hello";
    }
}

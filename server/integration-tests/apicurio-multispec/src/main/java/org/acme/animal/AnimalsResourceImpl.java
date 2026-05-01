package org.acme.animal;

import jakarta.ws.rs.core.Response;

public class AnimalsResourceImpl implements AnimalsResource {

    @Override
    public Response listAnimals() {
        return Response.ok().build();
    }
}

package org.acme.petstore;

import java.io.File;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.acme.petstore.model.ModelApiResponse;
import org.acme.petstore.model.Pet;
import org.acme.petstore.resources.PetResource;

public class DefaultPetResource implements PetResource {

    @Override
    public Pet addPet(Pet pet) {
        return pet;
    }

    @Override
    public Response deletePet(Long petId, String apiKey) {
        return Response.noContent().build();
    }

    @Override
    public List<Pet> findPetsByStatus(String status) {
        return List.of();
    }

    @Override
    public List<Pet> findPetsByTags(List<String> tags) {
        return List.of();
    }

    @Override
    public Pet getPetById(Long petId) {
        return null;
    }

    @Override
    public Pet updatePet(Pet pet) {
        return pet;
    }

    @Override
    public Response updatePetWithForm(Long petId, String name, String status) {
        return Response.noContent().build();
    }

    @Override
    public ModelApiResponse uploadFile(Long petId, String additionalMetadata, File body) {
        return null;
    }
}

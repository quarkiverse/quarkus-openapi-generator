package io.petstore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.petstore.beans.ApiResponse;
import io.petstore.beans.Pet;

public class PetStoreImpl implements PetResource {

    private static final Map<Long, Pet> PETS = new HashMap<>();

    @Override
    public Pet updatePet(Pet data) {
        return PETS.put(data.getId(), data);
    }

    @Override
    public Pet addPet(Pet data) {
        return PETS.put(data.getId(), data);
    }

    @Override
    public List<Pet> findPetsByStatus(String status) {
        return null;
    }

    @Override
    public List<Pet> findPetsByTags(List<String> tags) {
        return null;
    }

    @Override
    public Pet getPetById(long petId) {
        return PETS.get(petId);
    }

    @Override
    public void updatePetWithForm(long petId, String name, String status) {

    }

    @Override
    public void deletePet(String apiKey, long petId) {
        PETS.remove(petId);
    }

    @Override
    public ApiResponse uploadFile(long petId, String additionalMetadata, InputStream data) {
        return null;
    }
}

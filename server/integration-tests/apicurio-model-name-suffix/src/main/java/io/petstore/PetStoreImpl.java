package io.petstore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.petstore.beans.ApiResponseDto;
import io.petstore.beans.PetDto;

public class PetStoreImpl implements PetResource {

    private static final Map<Long, PetDto> PETS = new HashMap<>();

    @Override
    public PetDto updatePet(PetDto data) {
        return PETS.put(data.getId(), data);
    }

    @Override
    public PetDto addPet(PetDto data) {
        return PETS.put(data.getId(), data);
    }

    @Override
    public List<PetDto> findPetsByStatus(String status) {
        return null;
    }

    @Override
    public List<PetDto> findPetsByTags(List<String> tags) {
        return null;
    }

    @Override
    public PetDto getPetById(long petId) {
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
    public ApiResponseDto uploadFile(long petId, String additionalMetadata, InputStream data) {
        return null;
    }
}

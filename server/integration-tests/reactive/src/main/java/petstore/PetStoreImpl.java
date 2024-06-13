package petstore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.petstore.PetResource;
import io.petstore.beans.ApiResponse;
import io.petstore.beans.Pet;

public class PetStoreImpl implements PetResource {

    private static final Map<Long, Pet> PETS = new HashMap<>();

    @Override
    public CompletionStage<Pet> updatePet(Pet data) {
        return CompletableFuture.completedFuture(PETS.put(data.getId(), data));
    }

    @Override
    public CompletionStage<Pet> addPet(Pet data) {
        return CompletableFuture.completedFuture(PETS.put(data.getId(), data));
    }

    @Override
    public CompletionStage<List<Pet>> findPetsByStatus(String status) {
        return null;
    }

    @Override
    public CompletionStage<List<Pet>> findPetsByTags(List<String> tags) {
        return null;
    }

    @Override
    public CompletionStage<Pet> getPetById(long petId) {
        return CompletableFuture.completedFuture(PETS.get(petId));
    }

    @Override
    public CompletionStage<Void> updatePetWithForm(long petId, String name, String status) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> deletePet(String apiKey, long petId) {
        PETS.remove(petId);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<ApiResponse> uploadFile(long petId, String additionalMetadata, InputStream data) {
        return null;
    }
}

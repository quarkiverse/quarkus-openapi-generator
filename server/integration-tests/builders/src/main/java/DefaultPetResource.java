import java.io.InputStream;
import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import io.petstore.PetResource;
import io.petstore.beans.ApiResponse;
import io.petstore.beans.Pet;

public class DefaultPetResource implements PetResource {

    @Override
    public Pet updatePet(Pet data) {
        return null;
    }

    @Override
    public Pet addPet(Pet data) {
        return null;
    }

    @Override
    public RestResponse<List<Pet>> findPetsByStatus(String status) {
        return RestResponse.status(RestResponse.Status.OK);
    }

    @Override
    public List<Pet> findPetsByTags(List<String> tags) {
        return List.of();
    }

    @Override
    public Pet getPetById(long petId) {
        return null;
    }

    @Override
    public void updatePetWithForm(long petId, String name, String status) {

    }

    @Override
    public void deletePet(String apiKey, long petId) {

    }

    @Override
    public ApiResponse uploadFile(long petId, String additionalMetadata, InputStream data) {
        return null;
    }
}

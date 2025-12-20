package resources;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.Response;

import io.petstore.model.*;
import io.petstore.resources.*;

public class DefaultPetResource implements PetResource {

    private static final Map<Long, Pet> PET_STORE_DB = new ConcurrentHashMap<>();
    Random random = new Random();

    @Override
    public Pet addPet(Pet pet) {
        long l = random.nextLong();
        pet.setId(l);
        PET_STORE_DB.put(l, pet);
        return PET_STORE_DB.get(l);
    }

    @Override
    public Response deletePet(Long petId, String apiKey) {
        PET_STORE_DB.remove(petId);
        return Response.noContent().build();
    }

    @Override
    public List<Pet> findPetsByStatus(String status) {
        Pet.StatusEnum petStatus = Pet.StatusEnum.valueOf(status);
        return PET_STORE_DB.values().stream().filter(pet -> pet.getStatus().equals(petStatus)).toList();
    }

    @Override
    public List<Pet> findPetsByTags(List<String> tags) {
        return PET_STORE_DB.values().stream().filter(pet -> {
            if (pet.getTags() == null || pet.getTags().isEmpty()) {
                return false;
            }
            for (Tag tag : pet.getTags()) {
                if (tags.contains(tag.getName())) {
                    return true;
                }
            }
            return false;
        }).toList();
    }

    @Override
    public Pet getPetById(Long petId) {
        return PET_STORE_DB.get(petId);
    }

    @Override
    public Pet updatePet(Pet pet) {
        PET_STORE_DB.put(pet.getId(), pet);
        return PET_STORE_DB.get(pet.getId());
    }

    @Override
    public Response updatePetWithForm(Long petId, String name, String status) {
        Pet pet = PET_STORE_DB.get(petId);
        if (pet != null) {
            if (name != null) {
                pet.setName(name);
            }
            if (status != null) {
                pet.setStatus(Pet.StatusEnum.valueOf(status));
            }
            PET_STORE_DB.put(petId, pet);
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public ModelApiResponse uploadFile(Long petId, String additionalMetadata, File body) {
        ModelApiResponse response = new ModelApiResponse();
        response.setCode(200);
        response.setType("success");
        response.setMessage("File uploaded for petId: " + petId);
        return response;
    }
}

package resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.model.Category;
import org.acme.model.ModelApiResponse;
import org.acme.model.Pet;
import org.acme.model.Tag;
import org.acme.resources.PetResource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

public class DefaultPetResource implements PetResource {

    private static final Map<Long, Pet> PET_STORE_DB = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public Uni<Pet> addPet(Pet pet) {
        long id = Math.abs(random.nextLong());

        Pet createdPet = new Pet()
                .id(id)
                .name(pet != null ? pet.getName() : "Melina")
                .category(new Category().id(id).name("Dog"))
                .addTagsItem(new Tag().id(id).name("SMALL"))
                .photoUrls(List.of("https://picsum.photos/id/237/200/300"))
                .status(Pet.StatusEnum.AVAILABLE);

        PET_STORE_DB.put(id, createdPet);
        return Uni.createFrom().item(createdPet);
    }

    @Override
    public Uni<Void> deletePet(Long petId, String apiKey) {
        if (petId != null) {
            PET_STORE_DB.remove(petId);
        }
        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<List<Pet>> findPetsByStatus(String status) {
        Pet.StatusEnum statusEnum = Pet.StatusEnum.fromString(status);

        List<Pet> pets = new ArrayList<>();
        for (Pet pet : PET_STORE_DB.values()) {
            if (statusEnum.equals(pet.getStatus())) {
                pets.add(pet);
            }
        }
        return Uni.createFrom().item(pets);
    }

    @Override
    public Uni<List<Pet>> findPetsByTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Uni.createFrom().item(List.of());
        }

        Set<String> tagSet = new HashSet<>(tags);
        List<Pet> pets = new ArrayList<>();

        for (Pet pet : PET_STORE_DB.values()) {
            if (pet.getTags() != null &&
                    pet.getTags().stream().anyMatch(t -> tagSet.contains(t.getName()))) {
                pets.add(pet);
            }
        }
        return Uni.createFrom().item(pets);
    }

    @Override
    public Uni<Pet> getPetById(Long petId) {
        return Uni.createFrom().item(petId != null ? PET_STORE_DB.get(petId) : null);
    }

    @Override
    public Uni<Pet> updatePet(Pet pet) {
        if (pet == null || pet.getId() == null) {
            return Uni.createFrom().item((Pet) null);
        }

        return Uni.createFrom().item(
                PET_STORE_DB.computeIfPresent(pet.getId(), (id, existing) -> pet));
    }

    @Override
    public Uni<Void> updatePetWithForm(Long petId, String name, String status) {
        if (petId == null) {
            return Uni.createFrom().voidItem();
        }

        Pet.StatusEnum statusEnum = Pet.StatusEnum.fromString(status);
        PET_STORE_DB.computeIfPresent(petId, (id, pet) -> {
            pet.status(statusEnum);
            if (name != null) {
                pet.name(name);
            }
            return pet;
        });

        return Uni.createFrom().voidItem();
    }

    @Override
    public Uni<ModelApiResponse> uploadFile(Long petId, String additionalMetadata, File body) {
        Log.infof("Ignoring the file: %s", body);
        return Uni.createFrom()
                .item(new ModelApiResponse().code(200).message("Success").type("SUCCESS"));
    }
}

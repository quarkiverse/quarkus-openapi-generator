package resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.Response;

import org.acme.model.Category;
import org.acme.model.ModelApiResponse;
import org.acme.model.Pet;
import org.acme.model.Tag;
import org.acme.resources.PetResource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

// tag::content[]
public class DefaultPetResource implements PetResource {

    private static final Map<Long, Pet> PET_STORE_DB = new ConcurrentHashMap<>();
    Random random = new Random();

    @Override
    public Uni<Pet> addPet(Pet pet) {
        long l = random.nextLong();
        Pet createdPet = new Pet()
                .id(l)
                .name("Melina")
                .category(new Category().id(l).name("Dog"))
                .addTagsItem(new Tag().id(l).name("SMALL"))
                .photoUrls(List.of("https://picsum.photos/id/237/200/300"))
                .status(Pet.StatusEnum.AVAILABLE);

        return Uni.createFrom().item(PET_STORE_DB.put(l, createdPet));
    }

    @Override
    public Uni<Response> deletePet(Long petId, String apiKey) {
        return Uni.createFrom().item(Response.noContent().build());
    }

    @Override
    public Uni<List<Pet>> findPetsByStatus(String status) {
        List<Pet> pets = new ArrayList<>();
        for (Map.Entry<Long, Pet> entry : PET_STORE_DB.entrySet()) {
            if (entry.getValue().getStatus() == Pet.StatusEnum.fromString(status)) {
                pets.add(entry.getValue());
            }
        }
        return Uni.createFrom().item(pets);
    }

    @Override
    public Uni<List<Pet>> findPetsByTags(List<String> tags) {
        Set<String> allTags = new HashSet<>(tags);
        List<Pet> pets = new ArrayList<>();
        for (Map.Entry<Long, Pet> entry : PET_STORE_DB.entrySet()) {
            if (entry.getValue().getTags().stream().anyMatch(t -> allTags.contains(t.getName()))) {
                pets.add(entry.getValue());
            }
        }
        return Uni.createFrom().item(pets);
    }

    @Override
    public Uni<Pet> getPetById(Long petId) {
        return Uni.createFrom().item(PET_STORE_DB.get(petId));
    }

    @Override
    public Uni<Pet> updatePet(Pet pet) {
        return Uni.createFrom().item(PET_STORE_DB.compute(pet.getId(), (aLong, pet1) -> pet));
    }

    @Override
    public Uni<Response> updatePetWithForm(Long petId, String name, String status) {
        Pet computed = PET_STORE_DB.compute(petId, (aLong, pet) -> pet.status(Pet.StatusEnum.fromString(status)).name(name));
        return Uni.createFrom().item(Response.ok().entity(computed).build());
    }

    @Override
    public Uni<ModelApiResponse> uploadFile(Long petId, String additionalMetadata, File body) {
        Log.info("Ignoring the file: {}" + body);
        return Uni.createFrom().item(new ModelApiResponse().code(200).message("Success").type("SUCCESS"));
    }
}
// end::content[]
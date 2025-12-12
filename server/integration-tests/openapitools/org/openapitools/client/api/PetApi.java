package org.openapitools.client.api;

import java.util.List;
import java.util.Map;


import java.io.File;
import org.openapitools.client.model.ModelApiResponse;
import org.openapitools.client.model.Pet;
/**
  * Swagger Petstore - OpenAPI 3.0
  * <p>This is a sample Pet Store Server based on the OpenAPI 3.0 specification.  You can find out more about Swagger at [http://swagger.io](http://swagger.io). In the third iteration of the pet store, we've switched to the design first approach! You can now help us improve the API whether it's by making changes to the definition itself or to the code. That way, with time, we can improve the API in general, and expose some of the new features in OAS3.  Some useful links: - [The Pet Store repository](https://github.com/swagger-api/swagger-petstore) - [The source API definition for the Pet Store](https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml)</p>
  */
@jakarta.ws.rs.Path("/pet")
@org.eclipse.microprofile.rest.client.inject.RegisterRestClient(configKey="")
@jakarta.enterprise.context.ApplicationScoped
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface PetApi {

     /**
     * Add a new pet to the store
     *
     * Add a new pet to the store
     *
     * @param pet Create a new pet in the store
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Consumes({"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("addPet")
    public Pet addPet(
        Pet pet
    );

     /**
     * Deletes a pet
     *
     * @param petId Pet id to delete
     * @param apiKey 
     */
    @jakarta.ws.rs.DELETE
    @jakarta.ws.rs.Path("/{petId}")
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("deletePet")
    public jakarta.ws.rs.core.Response deletePet(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("petId") @jakarta.ws.rs.PathParam("petId")Long petId, 
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("api_key") @jakarta.ws.rs.HeaderParam("api_key")String apiKey
    );

     /**
     * Finds Pets by status
     *
     * Multiple status values can be provided with comma separated strings
     *
     * @param status Status values that need to be considered for filter
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/findByStatus")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("findPetsByStatus")
    public List<Pet> findPetsByStatus(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("status") @jakarta.ws.rs.QueryParam("status") String status
    );

     /**
     * Finds Pets by tags
     *
     * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
     *
     * @param tags Tags to filter by
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/findByTags")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("findPetsByTags")
    public List<Pet> findPetsByTags(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("tags") @jakarta.ws.rs.QueryParam("tags") List<String> tags
    );

     /**
     * Find pet by ID
     *
     * Returns a single pet
     *
     * @param petId ID of pet to return
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/{petId}")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("getPetById")
    public Pet getPetById(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("petId") @jakarta.ws.rs.PathParam("petId")Long petId
    );

     /**
     * Update an existing pet
     *
     * Update an existing pet by Id
     *
     * @param pet Update an existent pet in the store
     */
    @jakarta.ws.rs.PUT
    @jakarta.ws.rs.Consumes({"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("updatePet")
    public Pet updatePet(
        Pet pet
    );

     /**
     * Updates a pet in the store with form data
     *
     * @param petId ID of pet that needs to be updated
     * @param name Name of pet that needs to be updated
     * @param status Status of pet that needs to be updated
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Path("/{petId}")
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("updatePetWithForm")
    public jakarta.ws.rs.core.Response updatePetWithForm(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("petId") @jakarta.ws.rs.PathParam("petId")Long petId, 
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("name") @jakarta.ws.rs.QueryParam("name") String name, 
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("status") @jakarta.ws.rs.QueryParam("status") String status
    );

     /**
     * uploads an image
     *
     * @param petId ID of pet to update
     * @param additionalMetadata Additional Metadata
     * @param body 
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Path("/{petId}/uploadImage")
    @jakarta.ws.rs.Consumes({"application/octet-stream"})
    @jakarta.ws.rs.Produces({"application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("uploadFile")
    public ModelApiResponse uploadFile(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("petId") @jakarta.ws.rs.PathParam("petId")Long petId, 
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("additionalMetadata") @jakarta.ws.rs.QueryParam("additionalMetadata") String additionalMetadata, 
        File body
    );

}

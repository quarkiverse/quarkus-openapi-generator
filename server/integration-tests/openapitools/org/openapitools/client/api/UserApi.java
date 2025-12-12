package org.openapitools.client.api;

import java.util.List;
import java.util.Map;


import java.util.Date;
import org.openapitools.client.model.User;
/**
  * Swagger Petstore - OpenAPI 3.0
  * <p>This is a sample Pet Store Server based on the OpenAPI 3.0 specification.  You can find out more about Swagger at [http://swagger.io](http://swagger.io). In the third iteration of the pet store, we've switched to the design first approach! You can now help us improve the API whether it's by making changes to the definition itself or to the code. That way, with time, we can improve the API in general, and expose some of the new features in OAS3.  Some useful links: - [The Pet Store repository](https://github.com/swagger-api/swagger-petstore) - [The source API definition for the Pet Store](https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml)</p>
  */
@jakarta.ws.rs.Path("/user")
@org.eclipse.microprofile.rest.client.inject.RegisterRestClient(configKey="")
@jakarta.enterprise.context.ApplicationScoped
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface UserApi {

     /**
     * Create user
     *
     * This can only be done by the logged in user.
     *
     * @param user Created user object
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Consumes({"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @jakarta.ws.rs.Produces({"application/json", "application/xml"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("createUser")
    public User createUser(
        User user
    );

     /**
     * Creates list of users with given input array
     *
     * Creates list of users with given input array
     *
     * @param user 
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Path("/createWithList")
    @jakarta.ws.rs.Consumes({"application/json"})
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("createUsersWithListInput")
    public User createUsersWithListInput(
        List<@Valid User> user
    );

     /**
     * Delete user
     *
     * This can only be done by the logged in user.
     *
     * @param username The name that needs to be deleted
     */
    @jakarta.ws.rs.DELETE
    @jakarta.ws.rs.Path("/{username}")
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("deleteUser")
    public jakarta.ws.rs.core.Response deleteUser(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("username") @jakarta.ws.rs.PathParam("username")String username
    );

     /**
     * Get user by user name
     *
     * @param username The name that needs to be fetched. Use user1 for testing. 
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/{username}")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("getUserByName")
    public User getUserByName(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("username") @jakarta.ws.rs.PathParam("username")String username
    );

     /**
     * Logs user into the system
     *
     * @param username The user name for login
     * @param password The password for login in clear text
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/login")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("loginUser")
    public String loginUser(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("username") @jakarta.ws.rs.QueryParam("username") String username, 
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("password") @jakarta.ws.rs.QueryParam("password") String password
    );

     /**
     * Logs out current logged in user session
     *
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/logout")
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("logoutUser")
    public jakarta.ws.rs.core.Response logoutUser(
    );

     /**
     * Update user
     *
     * This can only be done by the logged in user.
     *
     * @param username name that need to be deleted
     * @param user Update an existent user in the store
     */
    @jakarta.ws.rs.PUT
    @jakarta.ws.rs.Path("/{username}")
    @jakarta.ws.rs.Consumes({"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("updateUser")
    public jakarta.ws.rs.core.Response updateUser(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("username") @jakarta.ws.rs.PathParam("username")String username, 
        User user
    );

}

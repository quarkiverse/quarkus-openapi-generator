package org.openapitools.client.api;

import java.util.List;
import java.util.Map;


import org.openapitools.client.model.Order;
/**
  * Swagger Petstore - OpenAPI 3.0
  * <p>This is a sample Pet Store Server based on the OpenAPI 3.0 specification.  You can find out more about Swagger at [http://swagger.io](http://swagger.io). In the third iteration of the pet store, we've switched to the design first approach! You can now help us improve the API whether it's by making changes to the definition itself or to the code. That way, with time, we can improve the API in general, and expose some of the new features in OAS3.  Some useful links: - [The Pet Store repository](https://github.com/swagger-api/swagger-petstore) - [The source API definition for the Pet Store](https://github.com/swagger-api/swagger-petstore/blob/master/src/main/resources/openapi.yaml)</p>
  */
@jakarta.ws.rs.Path("/store")
@org.eclipse.microprofile.rest.client.inject.RegisterRestClient(configKey="")
@jakarta.enterprise.context.ApplicationScoped
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface StoreApi {

     /**
     * Delete purchase order by ID
     *
     * For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors
     *
     * @param orderId ID of the order that needs to be deleted
     */
    @jakarta.ws.rs.DELETE
    @jakarta.ws.rs.Path("/order/{orderId}")
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("deleteOrder")
    public jakarta.ws.rs.core.Response deleteOrder(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("orderId") @jakarta.ws.rs.PathParam("orderId")Long orderId
    );

     /**
     * Returns pet inventories by status
     *
     * Returns a map of status codes to quantities
     *
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/inventory")
    @jakarta.ws.rs.Produces({"application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("getInventory")
    public Map<String, Integer> getInventory(
    );

     /**
     * Find purchase order by ID
     *
     * For valid response try integer IDs with value <= 5 or > 10. Other values will generate exceptions.
     *
     * @param orderId ID of order that needs to be fetched
     */
    @jakarta.ws.rs.GET
    @jakarta.ws.rs.Path("/order/{orderId}")
    @jakarta.ws.rs.Produces({"application/xml", "application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("getOrderById")
    public Order getOrderById(
        @io.quarkiverse.openapi.generator.annotations.GeneratedParam("orderId") @jakarta.ws.rs.PathParam("orderId")Long orderId
    );

     /**
     * Place an order for a pet
     *
     * Place a new order in the store
     *
     * @param order 
     */
    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Path("/order")
    @jakarta.ws.rs.Consumes({"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @jakarta.ws.rs.Produces({"application/json"})
    @io.quarkiverse.openapi.generator.annotations.GeneratedMethod("placeOrder")
    public Order placeOrder(
        Order order
    );

}

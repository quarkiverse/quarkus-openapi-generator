package org.acme;

import java.util.List;

import jakarta.ws.rs.core.Response;

public class InventoryResourceImpl implements InventoryResource {

    @Override
    public Response listInventory() {
        return Response.ok(List.of()).build();
    }
}

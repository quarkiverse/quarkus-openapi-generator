package org.acme.orders;

import java.util.List;

import jakarta.ws.rs.core.Response;

public class OrdersResourceImpl implements OrdersResource {

    @Override
    public Response listOrders() {
        return Response.ok(List.of()).build();
    }
}

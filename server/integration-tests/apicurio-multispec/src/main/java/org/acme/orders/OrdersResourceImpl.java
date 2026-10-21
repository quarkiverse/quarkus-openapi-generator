package org.acme.orders;

import java.util.List;

import org.acme.orders.beans.PurchaseOrder;

public class OrdersResourceImpl implements OrdersResource {

    @Override
    public List<PurchaseOrder> listOrders() {
        return List.of();
    }
}

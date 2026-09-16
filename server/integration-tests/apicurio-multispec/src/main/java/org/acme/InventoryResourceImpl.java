package org.acme;

import java.util.List;

import org.acme.beans.Product;

public class InventoryResourceImpl implements InventoryResource {

    @Override
    public List<Product> listInventory() {
        return List.of();
    }
}

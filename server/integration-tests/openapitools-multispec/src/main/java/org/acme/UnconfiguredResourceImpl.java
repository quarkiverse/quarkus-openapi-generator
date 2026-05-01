package org.acme;

import java.util.List;

import org.acme.model.Item;
import org.acme.resources.DefaultResource;

public class UnconfiguredResourceImpl implements DefaultResource {

    @Override
    public List<Item> listUnconfigured() {
        return List.of();
    }
}

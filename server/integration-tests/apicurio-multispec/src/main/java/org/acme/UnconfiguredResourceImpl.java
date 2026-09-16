package org.acme;

import java.util.List;

import org.acme.beans.Item;

public class UnconfiguredResourceImpl implements UnconfiguredResource {

    @Override
    public List<Item> listUnconfigured() {
        return List.of();
    }
}

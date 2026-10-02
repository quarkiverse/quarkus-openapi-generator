package org.acme;

import java.util.List;

import org.acme.beans.Animal;

public class AnimalsResourceImpl implements AnimalsResource {

    @Override
    public List<Animal> listAnimals() {
        return List.of();
    }
}

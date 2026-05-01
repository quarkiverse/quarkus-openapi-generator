package org.acme.animal;

import java.util.List;

import org.acme.animal.model.Animal;
import org.acme.animal.resources.DefaultResource;

public class DefaultAnimalsResource implements DefaultResource {

    @Override
    public List<Animal> listAnimals() {
        return List.of();
    }
}

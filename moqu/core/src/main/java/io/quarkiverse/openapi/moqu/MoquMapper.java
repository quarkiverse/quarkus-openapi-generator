package io.quarkiverse.openapi.moqu;

import java.util.List;

/**
 * A generic interface for mapping a {@link Moqu} instance to a list of objects of type {@code T}.
 *
 * @param <T> the type of objects to which the {@link Moqu} instance will be mapped.
 */
public interface MoquMapper<T> {

    /**
     * Maps the given {@link Moqu} instance to a list of objects of type {@code T}.
     *
     * @param moqu the {@link Moqu} instance to be mapped.
     * @return a list of mapped objects of type {@code T}.
     */
    List<T> map(Moqu moqu);
}
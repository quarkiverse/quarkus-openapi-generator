package io.quarkiverse.openapi.moqu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.quarkiverse.openapi.moqu.model.RequestResponsePair;

/**
 * Represents a collection of request-response pairs, providing methods to access
 * these pairs in an immutable list.
 */
public class Moqu {

    private List<RequestResponsePair> requestResponsePairs = new ArrayList<>();

    /**
     * Constructs a {@code Moqu} instance with the provided list of request-response pairs.
     *
     * @param requestResponsePairs the list of {@link RequestResponsePair} objects to initialize
     *        the collection. Must not be {@code null}.
     * @throws NullPointerException if {@code requestResponsePairs} is null.
     */
    public Moqu(List<RequestResponsePair> requestResponsePairs) {
        this.requestResponsePairs = Objects.requireNonNull(requestResponsePairs);
    }

    /**
     * Returns an unmodifiable list of request-response pairs.
     *
     * @return an immutable list of {@link RequestResponsePair}.
     */
    public List<RequestResponsePair> getRequestResponsePairs() {
        return Collections.unmodifiableList(requestResponsePairs);
    }
}

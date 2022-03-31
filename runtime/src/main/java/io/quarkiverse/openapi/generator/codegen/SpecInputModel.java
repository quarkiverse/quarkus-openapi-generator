package io.quarkiverse.openapi.generator.codegen;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.Objects;

public class SpecInputModel {

    private final InputStream inputStream;
    private final String filename;

    public SpecInputModel(final String filename, final InputStream inputStream) {
        requireNonNull(inputStream, "InputStream can't be null");
        requireNonNull(filename, "File name can't be null");
        this.inputStream = inputStream;
        this.filename = filename;
    }

    public String getFileName() {
        return filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return "SpecInputModel{" +
                "name='" + filename + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SpecInputModel that = (SpecInputModel) o;
        return inputStream.equals(that.inputStream) && filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputStream, filename);
    }
}

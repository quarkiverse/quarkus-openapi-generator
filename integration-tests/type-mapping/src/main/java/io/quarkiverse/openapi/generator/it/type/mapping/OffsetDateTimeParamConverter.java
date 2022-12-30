package io.quarkiverse.openapi.generator.it.type.mapping;

import java.time.OffsetDateTime;

import javax.ws.rs.ext.ParamConverter;

public class OffsetDateTimeParamConverter implements ParamConverter<OffsetDateTime> {

    @Override
    public OffsetDateTime fromString(String value) {
        return value == null ? null : OffsetDateTime.parse(value);
    }

    @Override
    public String toString(OffsetDateTime value) {
        return value == null ? null : value.toString();
    }
}

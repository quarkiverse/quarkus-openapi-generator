package io.quarkiverse.openapi.generator.it.type.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class OffsetDateTimeParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (OffsetDateTime.class.equals(rawType)) {
            return (ParamConverter<T>) new OffsetDateTimeParamConverter();
        }
        return null;
    }
}

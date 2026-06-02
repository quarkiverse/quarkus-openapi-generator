package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.acme.one.of.interfaces.model.OtherThing;
import org.acme.one.of.interfaces.model.SomeThing;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UseOneOfInterfacesTest {

    @Inject
    ObjectMapper mapper;

    @Test
    void oneOfPropertyIsInterface() {
        assertThat(org.acme.one.of.interfaces.model.HolderThing.class.isInterface()).isTrue();
    }

    @Test
    void oneOfMembersImplementInterface() {
        assertThat(org.acme.one.of.interfaces.model.HolderThing.class)
                .isAssignableFrom(org.acme.one.of.interfaces.model.SomeThing.class);
        assertThat(org.acme.one.of.interfaces.model.HolderThing.class)
                .isAssignableFrom(org.acme.one.of.interfaces.model.OtherThing.class);
    }

    @Test
    void someThingSerializes() throws JsonProcessingException {
        var thing = new SomeThing()
                .some("hello");

        var json = mapper.writeValueAsString(thing);

        assertThat(json).isEqualTo("{\"some\":\"hello\"}");
    }

    @Test
    void otherThingDeserializes() throws JsonProcessingException {
        var thing = new OtherThing()
                .other("world");

        var json = mapper.writeValueAsString(thing);

        assertThat(json).isEqualTo("{\"other\":\"world\"}");
    }

    @Test
    void holderWithOtherThingSerializes() throws JsonProcessingException {
        var thing = new org.acme.one.of.interfaces.model.Holder()
                .id(1L)
                .thing(new OtherThing()
                        .other("world"));

        var json = mapper.writeValueAsString(thing);

        assertThat(json).isEqualTo("{\"id\":1,\"thing\":{\"other\":\"world\"}}");
    }

    @Test
    void holderWithSomeThingDeserializes() throws JsonProcessingException {
        var json = "{\"id\":1,\"thing\":{\"some\":\"hello\"}}";

        var holder = mapper.readValue(json, org.acme.one.of.interfaces.model.Holder.class);

        assertThat(holder.getId()).isEqualTo(1L);
        assertThat(holder.getThing())
                .asInstanceOf(InstanceOfAssertFactories.type(SomeThing.class))
                .extracting(SomeThing::getSome)
                .isEqualTo("hello");
    }
}

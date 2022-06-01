package com.pc.auth.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pc.auth.entities.User;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

            gen.writeNumberField("id", value.getId() == null ? 0 : value.getId());
            gen.writeStringField("nombre", value.getNombre() == null ? "" : value.getNombre());
            gen.writeStringField("apellido", value.getApellido() == null ? "" : value.getApellido());
            gen.writeStringField("username", value.getUsername() == null ? "" : value.getUsername());
            gen.writeObjectField("rol", value.getRol() == null ? "" : value.getRol());
            gen.writeBooleanField("enabled", value.isEnabled());

        gen.writeEndObject();
    }
}

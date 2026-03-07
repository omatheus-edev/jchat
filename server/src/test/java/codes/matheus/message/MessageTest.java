package codes.matheus.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

final class MessageTest {
    @Test
    void serializeTest() {
        @NotNull Instant instant = Instant.parse("2026-03-03T20:00:00Z");
        @NotNull Message message = new Message(Type.GERAL, "oi pessoal", "skar", instant);

        @NotNull String json = message.serialize();

        assertTrue(json.contains("\"type\":\"GERAL\""));
        assertTrue(json.contains("\"sender\":\"skar\""));
        assertTrue(json.contains("\"content\":\"oi pessoal\""));
        assertTrue(json.contains("\"instant\":"));
        System.out.println(json);
    }

    @Test
    void deserializeTest() {
        @NotNull String json = "{\"type\":\"GERAL\",\"content\":\"oi pessoal\",\"instant\":\"2026-03-03T20:00:00Z\"}";

        @Nullable Message message = Message.deserialize(json, "skar");

        assertNotNull(message);
        assertEquals(Type.GERAL, message.getType());
        assertEquals("oi pessoal", message.getContent());
        assertEquals("skar", message.getSender());
    }

}
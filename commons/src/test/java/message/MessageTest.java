package message;

import codes.matheus.message.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

final class MessageTest {
    @Test
    void testValidateSerialize() {
        @NotNull Instant instant = Instant.parse("2026-02-21T23:00:00Z");
        @NotNull String expectedTime = "21/02/2026 20:00:00";

        @NotNull MessageRequest request = Message.create(Message.Type.REQUEST, Message.Operation.AUTH, "skar:password123", instant);
        assertEquals("skar:password123", request.getContent());
        assertEquals("{\"content\":\"skar:password123\",\"instant\":\"" + expectedTime + "\"}", request.toJson().toString());

        @NotNull MessageResponse response = Message.create(Message.Type.RESPONSE, Message.Operation.AUTH, "201|Successfully authenticated", instant);
        assertEquals(MessageStatus.CREATED, response.getStatus());
        assertEquals("{\"status\":\"CREATED\",\"content\":\"Successfully authenticated\",\"instant\":\"" + expectedTime + "\"}", response.toJson().toString());


        @NotNull MessageChat server = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "SERVER:skar joined the chat", instant);
        assertEquals("SERVER", server.getSender());
        assertEquals("{\"sender\":\"SERVER\",\"content\":\"skar joined the chat\",\"instant\":\"" + expectedTime + "\"}", server.toJson().toString());


        @NotNull MessageChat user = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "skar:hello guys", instant);
        assertEquals("skar", user.getSender());
        assertEquals("{\"sender\":\"skar\",\"content\":\"hello guys\",\"instant\":\"" + expectedTime + "\"}", user.toJson().toString());

        System.out.println(request.toJson());
        System.out.println(response.toJson());
        System.out.println(server.toJson());
        System.out.println(user.toJson());

    }
}
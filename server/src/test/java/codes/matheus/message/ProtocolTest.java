package codes.matheus.message;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

final class ProtocolTest {
    private final @NotNull Protocol protocol = new Protocol();
    private final @NotNull Instant instant = Instant.parse("2026-02-23T14:00:00Z");

    @Test
    void testEncode() {
        @NotNull MessageRequest request = Message.create(Message.Type.REQUEST, Message.Operation.AUTH_LOGIN, "skar:password123", instant);
        @NotNull MessageResponse response = Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_LOGIN, "201|Successfully authenticated", instant);
        @NotNull MessageChat server = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "SERVER:skar joined the chat", instant);
        @NotNull MessageChat user = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "skar:hello guys", instant);
        @NotNull String expectedTime = "23/02/2026 11:00:00";

        assertEquals("REQUEST\r\nAUTH_LOGIN\r\n\r\n{\"content\":\"skar:password123\",\"instant\":\"" + expectedTime + "\"}", protocol.encode(request));
        assertEquals("RESPONSE\r\nAUTH_LOGIN\r\n\r\n{\"status\":201,\"content\":\"Successfully authenticated\",\"instant\":\"" + expectedTime + "\"}", protocol.encode(response));
        assertEquals("CHAT\r\nBROADCAST\r\n\r\n{\"sender\":\"skar\",\"content\":\"hello guys\",\"instant\":\"" + expectedTime + "\"}", protocol.encode(user));
        assertEquals("CHAT\r\nBROADCAST\r\n\r\n{\"sender\":\"SERVER\",\"content\":\"skar joined the chat\",\"instant\":\"" + expectedTime + "\"}", protocol.encode(server));
    }

    @Test
    void testDecode() {
        @NotNull MessageRequest request = Message.create(Message.Type.REQUEST, Message.Operation.AUTH_LOGIN, "skar:password123", instant);
        @NotNull MessageResponse response = Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_LOGIN, "201|Successfully authenticated", instant);
        @NotNull MessageChat server = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "SERVER:skar joined the chat", instant);
        @NotNull MessageChat user = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "skar:hello guys", instant);
        @NotNull String encodeRequest = protocol.encode(request);
        @NotNull String encodeResponse = protocol.encode(response);
        @NotNull String encodeServer = protocol.encode(server);
        @NotNull String encodeUser = protocol.encode(user);

        assertEquals(request, protocol.decode(encodeRequest));
        assertEquals(response, protocol.decode(encodeResponse));
        assertEquals(server, protocol.decode(encodeServer));
        assertEquals(user, protocol.decode(encodeUser));
    }
}
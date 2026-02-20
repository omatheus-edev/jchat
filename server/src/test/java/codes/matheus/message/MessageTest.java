package codes.matheus.message;

import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class MessageTest {
    @Test
    void testInstance() {
        @NotNull Message auth = Message.create(Message.Type.AUTHORIZATION, null, "skar:password123");
        @NotNull Message server = Message.create(Message.Type.SERVER, null, "skar joined on chat");
        @NotNull Message chat = Message.create(Message.Type.CHAT, new User("skar", "password123"), "hello");

        System.out.println(auth);
        System.out.println(server);
        System.out.println(chat);
    }

    @Test
    void testValidate() {
        assertTrue(MessageAuth.validate("skar:password123"));
        assertTrue(MessageAuth.validate("skar:password"));
        assertTrue(MessageAuth.validate("skar:senha123"));
    }
}
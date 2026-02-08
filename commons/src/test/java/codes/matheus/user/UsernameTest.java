package codes.matheus.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class UsernameTest {
    @Test
    void testValidateName() {
        assertFalse(Username.validate("a"));
        assertFalse(Username.validate(" "));
        assertFalse(Username.validate("g4mer%%"));
        assertTrue(Username.validate("admin"));
        assertTrue(Username.validate("@g4mer_#1"));

        assertNotNull(Username.create("skar"));
    }
}
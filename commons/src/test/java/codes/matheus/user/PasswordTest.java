package codes.matheus.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class PasswordTest {
    @Test
    void testValidate() {
        assertFalse(Password.validate("a"));
        assertFalse(Password.validate(" "));
        assertFalse(Password.validate("@%%"));
        assertFalse(Password.validate("124312"));
        assertFalse(Password.validate("agfrageaewfga"));
        assertTrue(Password.validate("password123"));
        assertTrue(Password.validate("password_123!"));
        assertTrue(Password.validate("a1!@#"));
    }
}
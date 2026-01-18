package htw.projekt.feelique.test;

import htw.projekt.feelique.rest.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        // Eingabedaten
        String email = "test@htw.de";
        String passwordHash = "TestPasswort";

        // System under test aufsetzen
        User user = new User(email, passwordHash);
        user.setId(1L);
        user.setSessionToken("abc-123");
        user.setFirstName("Max");
        user.setLastName("Mustermann");

        // Vergleich
        assertEquals(1L, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals("abc-123", user.getSessionToken());
        assertEquals("Max", user.getFirstName());
        assertEquals("Mustermann", user.getLastName());
    }
}
package htw.projekt.feelique;

import htw.projekt.feelique.business.repository.UserRepository;
import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.rest.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("removal")
@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("should register a new user successfully")
    void testRegister() {
        // Test Daten
        String email = "test@htw-berlin.de";
        String password = "password123";

        User savedUser = new User(email, BCrypt.hashpw(password, BCrypt.gensalt()));
        savedUser.setId(1L);
        savedUser.setSessionToken("test-token-123");

        doReturn(null).when(userRepository).findByEmail(email);
        doReturn(savedUser).when(userRepository).save(any(User.class));

        // Aufruf
        User result = authService.register(email, password);

        // Vergleich
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertNotNull(result.getSessionToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when email already exists")
    void testRegisterEmailExists() {
        // Test Daten
        String email = "existing@htw-berlin.de";
        User existingUser = new User(email, "hash");

        doReturn(existingUser).when(userRepository).findByEmail(email);

        // Aufruf und Vergleich
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(email, "password");
        });

        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    @DisplayName("should login user with correct credentials")
    void testLogin() {
        // Test Daten
        String email = "user@htw-berlin.de";
        String password = "correctPassword";
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(email, passwordHash);
        user.setId(1L);

        doReturn(user).when(userRepository).findByEmail(email);
        doReturn(user).when(userRepository).save(any(User.class));

        // Aufruf
        User result = authService.login(email, password);

        // Vergleich
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertNotNull(result.getSessionToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when user not found during login")
    void testLoginUserNotFound() {
        doReturn(null).when(userRepository).findByEmail("notfound@htw-berlin.de");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("notfound@htw-berlin.de", "password");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("should authenticate user with valid session token")
    void testAuthenticate() {
        // Test Daten
        String token = "valid-token-123";
        User user = new User("user@htw-berlin.de", "hash");
        user.setSessionToken(token);

        doReturn(user).when(userRepository).findBySessionToken(token);

        // Aufruf
        User result = authService.authenticate(token);

        // Vergleich
        assertNotNull(result);
        assertEquals(token, result.getSessionToken());
    }

    @Test
    @DisplayName("should throw exception when session token is invalid")
    void testAuthenticateInvalidToken() {
        doReturn(null).when(userRepository).findBySessionToken("invalid-token");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate("invalid-token");
        });

        assertEquals("Invalid session token", exception.getMessage());
    }

    @Test
    @DisplayName("should update user successfully")
    void testUpdateUser() {
        // Test Daten
        User user = new User("user@htw-berlin.de", "hash");
        user.setId(1L);
        user.setFirstName("Max");

        doReturn(user).when(userRepository).save(user);

        // Aufruf
        User result = authService.updateUser(user);

        // Vergleich
        assertNotNull(result);
        assertEquals("Max", result.getFirstName());
        verify(userRepository).save(user);
    }
}
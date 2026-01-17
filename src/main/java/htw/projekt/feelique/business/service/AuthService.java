package htw.projekt.feelique.business.service;

import htw.projekt.feelique.business.repository.UserRepository;
import htw.projekt.feelique.rest.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Registrierung
    public User register(String email, String password) {

        // 1. Prüfen, ob Email schon existiert
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already in use");
        }

        // 2. Passwort hashen
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // 3. User anlegen
        User user = new User(email, passwordHash);

        // 4. Session Token generieren
        user.setSessionToken(UUID.randomUUID().toString());

        // 5. Speichern
        return userRepository.save(user);
    }

    // Login
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Passwort prüfen
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect password");
        }

        // Neues Session Token erzeugen
        user.setSessionToken(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    // User anhand Session Token finden (für geschützte Endpoints)
    public User authenticate(String sessionToken) {

        if (sessionToken == null || sessionToken.isBlank()) {
            throw new RuntimeException("No session token provided");
        }

        User user = userRepository.findBySessionToken(sessionToken);

        if (user == null) {
            throw new RuntimeException("Invalid session token");
        }

        return user;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
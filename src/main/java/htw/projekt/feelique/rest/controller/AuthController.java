package htw.projekt.feelique.rest.controller;

import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.rest.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Registrierung
    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return authService.register(email, password);
    }

    // Login
    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return authService.login(email, password);
    }

    // NEU: Profil abrufen
    @GetMapping("/profile")
    public User getProfile(@RequestHeader("X-Session-Token") String token) {
        return authService.authenticate(token);
    }

    // NEU: Profil aktualisieren
    @PutMapping("/profile")
    public User updateProfile(
            @RequestHeader("X-Session-Token") String token,
            @RequestBody Map<String, String> body) {

        User user = authService.authenticate(token);

        if (body.containsKey("firstName")) {
            user.setFirstName(body.get("firstName"));
        }
        if (body.containsKey("lastName")) {
            user.setLastName(body.get("lastName"));
        }

        return authService.updateUser(user);
    }
}
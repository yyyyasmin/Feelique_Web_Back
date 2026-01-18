package htw.projekt.feelique;

import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.rest.controller.AuthController;
import htw.projekt.feelique.rest.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    public void testRegister() throws Exception {
        // Test Daten
        User user = new User("test@htw-berlin.de", "hashedPassword");
        user.setId(1L);
        user.setSessionToken("token-123");

        when(authService.register(anyString(), anyString())).thenReturn(user);

        // Erwartetes Ergebnis
        String requestBody = "{\"email\":\"test@htw-berlin.de\",\"password\":\"password123\"}";

        // Aufruf und Vergleich
        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@htw-berlin.de"))
                .andExpect(jsonPath("$.sessionToken").value("token-123"));
    }

    @Test
    public void testLogin() throws Exception {
        // Test Daten
        User user = new User("user@htw-berlin.de", "hashedPassword");
        user.setId(2L);
        user.setSessionToken("login-token-456");

        when(authService.login(anyString(), anyString())).thenReturn(user);

        // Request Body
        String requestBody = "{\"email\":\"user@htw-berlin.de\",\"password\":\"mypassword\"}";

        // Aufruf und Vergleich
        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@htw-berlin.de"))
                .andExpect(jsonPath("$.sessionToken").value("login-token-456"));
    }

    @Test
    public void testGetProfile() throws Exception {
        // Test Daten
        User user = new User("profile@htw-berlin.de", "hash");
        user.setId(3L);
        user.setFirstName("Max");
        user.setLastName("Mustermann");
        user.setSessionToken("profile-token");

        when(authService.authenticate("profile-token")).thenReturn(user);

        // Aufruf und Vergleich
        this.mockMvc.perform(get("/auth/profile")
                        .header("X-Session-Token", "profile-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("profile@htw-berlin.de"))
                .andExpect(jsonPath("$.firstName").value("Max"))
                .andExpect(jsonPath("$.lastName").value("Mustermann"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        // Test Daten
        User user = new User("update@htw-berlin.de", "hash");
        user.setId(4L);
        user.setFirstName("NewName");
        user.setLastName("NewLastName");

        when(authService.authenticate("update-token")).thenReturn(user);
        when(authService.updateUser(user)).thenReturn(user);

        String requestBody = "{\"firstName\":\"NewName\",\"lastName\":\"NewLastName\"}";

        // Aufruf und Vergleich
        this.mockMvc.perform(put("/auth/profile")
                        .header("X-Session-Token", "update-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value("NewLastName"));
    }
}
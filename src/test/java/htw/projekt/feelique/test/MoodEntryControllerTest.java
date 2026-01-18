package htw.projekt.feelique.test;

import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.controller.MoodEntryController;
import htw.projekt.feelique.rest.model.MoodEntry;
import htw.projekt.feelique.rest.model.MoodStatsDto;
import htw.projekt.feelique.rest.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(MoodEntryController.class)
public class MoodEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MoodEntryService moodEntryService;

    @MockBean
    private AuthService authService;


    @Test
    void testGetEntries() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        MoodEntry m1 = new MoodEntry();
        m1.setMood("Glücklich");

        MoodEntry m2 = new MoodEntry();
        m2.setMood("Traurig");

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getEntriesForUser(1L)).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/moods").header("X-Session-Token", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetEntriesUnauthorized() throws Exception {
        when(authService.authenticate("Sauer")).thenReturn(null);

        mockMvc.perform(get("/moods").header("X-Session-Token", "Sauer"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testCreateEntrySuccess() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        MoodEntry saved = new MoodEntry();
        saved.setMood("Glücklich");
        saved.setNote("Nice");

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getEntriesForUser(1L)).thenReturn(List.of());
        when(moodEntryService.saveMood(any())).thenReturn(saved);

        mockMvc.perform(post("/moods")
                        .header("X-Session-Token", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mood":"Glücklich",
                                  "time":"2025-01-01T10:00:00",
                                  "note":"Nice"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mood").value("Glücklich"));
    }

    @Test
    void testCreateEntryUnauthorized() throws Exception {
        when(authService.authenticate("Sauer")).thenReturn(null);

        mockMvc.perform(post("/moods")
                        .header("X-Session-Token", "Sauer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateEntryDuplicateDate() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        MoodEntry existing = new MoodEntry();
        existing.setTime(LocalDateTime.of(2025, 1, 1, 8, 0));

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getEntriesForUser(1L)).thenReturn(List.of(existing));

        mockMvc.perform(post("/moods")
                        .header("X-Session-Token", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mood":"Traurig",
                                  "time":"2025-01-01T12:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteEntrySuccess() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        MoodEntry entry = new MoodEntry();
        entry.setUserId(1L);

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getEntryById(1L)).thenReturn(Optional.of(entry));

        mockMvc.perform(delete("/moods/1")
                        .header("X-Session-Token", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteEntryForbidden() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        MoodEntry entry = new MoodEntry();
        entry.setUserId(2L);

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getEntryById(1L)).thenReturn(Optional.of(entry));

        mockMvc.perform(delete("/moods/1")
                        .header("X-Session-Token", "token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteEntryUnauthorized() throws Exception {
        when(authService.authenticate("Sauer")).thenReturn(null);

        mockMvc.perform(delete("/moods/1")
                        .header("X-Session-Token", "Sauer"))
                .andExpect(status().isUnauthorized());
    }

    /* ---------------- PUT /moods/{id} ---------------- */

    @Test
    void testUpdateMoodEntrySuccess() throws Exception {
        MoodEntry updated = new MoodEntry();
        updated.setMood("Aufgeregt");

        when(moodEntryService.updateMoodEntry(
                eq(1L), eq("Aufgeregt"), any(), eq("note"), eq("token")
        )).thenReturn(updated);

        mockMvc.perform(put("/moods/1")
                        .header("X-Session-Token", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mood":"Aufgeregt",
                                  "time":"2025-01-01T12:00:00",
                                  "note":"note"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mood").value("Aufgeregt"));
    }

    @Test
    void testUpdateMoodEntryForbidden() throws Exception {
        when(moodEntryService.updateMoodEntry(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Forbidden"));

        mockMvc.perform(put("/moods/1")
                        .header("X-Session-Token", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    /* ---------------- GET /moods/stats ---------------- */

    @Test
    void testGetMoodStatsSuccess() throws Exception {
        User user = new User("test@htw.de", "hash");
        user.setId(1L);

        when(authService.authenticate("token")).thenReturn(user);
        when(moodEntryService.getMoodStatsForUser(eq(1L), any(), any()))
                .thenReturn(Arrays.asList(
                        new MoodStatsDto("Glücklich", 2),
                        new MoodStatsDto("Traurig", 1)
                ));

        mockMvc.perform(get("/moods/stats")
                        .header("X-Session-Token", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetMoodStatsUnauthorized() throws Exception {
        when(authService.authenticate("Sauer")).thenReturn(null);

        mockMvc.perform(get("/moods/stats")
                        .header("X-Session-Token", "Sauer"))
                .andExpect(status().isUnauthorized());
    }
}

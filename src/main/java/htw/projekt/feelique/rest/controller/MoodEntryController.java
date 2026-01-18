package htw.projekt.feelique.rest.controller;

import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.model.MoodEntry;
import htw.projekt.feelique.rest.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/moods")
public class MoodEntryController {

    private final MoodEntryService moodEntryService;
    private final AuthService authService;

    public MoodEntryController(MoodEntryService moodEntryService, AuthService authService) {
        this.moodEntryService = moodEntryService;
        this.authService = authService;
    }

    // DTO für PUT-Request
    public static class MoodEntryDto {
        private String mood;
        private LocalDateTime time;
        private String note;

        public String getMood() { return mood; }
        public void setMood(String mood) { this.mood = mood; }
        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    // GET: Alle Einträge des eingeloggten Users abrufen (für Liste & Kalender)
    @GetMapping
    public ResponseEntity<List<MoodEntry>> getEntries(@RequestHeader("X-Session-Token") String token) {
        User user = authService.authenticate(token);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<MoodEntry> moods = moodEntryService.getEntriesForUser(user.getId());
        return ResponseEntity.ok(moods);
    }

    // POST: Mood für eingeloggten User speichern
    @PostMapping
    public ResponseEntity<?> createEntry(
            @RequestHeader("X-Session-Token") String token,
            @RequestBody MoodEntry entry) {

        User user = authService.authenticate(token);
        if (user == null) return ResponseEntity.status(401).build();

        // Prüfen, ob Datum in der Zukunft liegt
        if (entry.getTime().isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Du kannst keine Moods für die Zukunft eintragen!");
        }

        // Prüfen, ob heute schon ein Eintrag existiert
        List<MoodEntry> existingMoods = moodEntryService.getEntriesForUser(user.getId());
        String today = LocalDate.now().toString();

        boolean alreadyExists = existingMoods.stream()
                .anyMatch(e -> e.getTime().toLocalDate().toString().equals(today));

        if (alreadyExists) {
            return ResponseEntity.badRequest().body("Du hast heute bereits einen Mood eingetragen!");
        }

        entry.setUserId(user.getId());
        return ResponseEntity.ok(moodEntryService.saveMood(entry));
    }

    // DELETE: Nur eigene MoodEntries löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable Long id) {

        User user = authService.authenticate(token);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        MoodEntry entry = moodEntryService.getEntryById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUserId().equals(user.getId())) {
            return ResponseEntity.status(403).body("No permission to delete this entry");
        }

        moodEntryService.deleteMood(id);
        return ResponseEntity.ok().build();
    }

    // PUT: Mood bearbeiten
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMoodEntry(
            @PathVariable Long id,
            @RequestBody MoodEntryDto dto,
            @RequestHeader("X-Session-Token") String sessionToken
    ) {
        try {
            MoodEntry updated = moodEntryService.updateMoodEntry(
                    id,
                    dto.getMood(),
                    dto.getTime(),
                    dto.getNote(),
                    sessionToken
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            if (e.getMessage().equals("Forbidden")) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            if (e.getMessage().equals("Entry not found")) {
                return ResponseEntity.status(404).body("Entry not found");
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
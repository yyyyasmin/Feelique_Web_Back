package htw.projekt.feelique.rest.controller;

import htw.projekt.feelique.business.service.AuthService;
import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.model.MoodEntry;
import htw.projekt.feelique.rest.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moods")
public class MoodEntryController {

    private final MoodEntryService moodEntryService;
    private final AuthService authService;

    public MoodEntryController(MoodEntryService moodEntryService, AuthService authService) {
        this.moodEntryService = moodEntryService;
        this.authService = authService;
    }

    // GET: Nur Einträge des eingeloggten Users
    @GetMapping
    public Iterable<MoodEntry> getEntries(@RequestHeader("X-Session-Token") String token) {
        User user = authService.authenticate(token);
        return moodEntryService.getEntriesForUser(user.getId());
    }

    // POST: Mood für eingeloggten User speichern
    @PostMapping
    public MoodEntry createEntry(
            @RequestHeader("X-Session-Token") String token,
            @RequestBody MoodEntry entry) {

        User user = authService.authenticate(token);
        entry.setUserId(user.getId());
        return moodEntryService.saveMood(entry);
    }

    // DELETE: Nur eigene MoodEntries löschen
    @DeleteMapping("/{id}")
    public void deleteEntry(
            @RequestHeader("X-Session-Token") String token,
            @PathVariable Long id) {

        User user = authService.authenticate(token);

        MoodEntry entry = moodEntryService.getEntryById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getUserId().equals(user.getId())) {
            throw new RuntimeException("No permission to delete this entry");
        }

        moodEntryService.deleteMood(id);
    }
}
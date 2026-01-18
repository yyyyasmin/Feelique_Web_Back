package htw.projekt.feelique;

import htw.projekt.feelique.rest.model.MoodEntry;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MoodEntryTest {

    @Test
    void testGettersAndSetters() {
        // Eingabedaten
        String mood = "Glücklich";
        LocalDateTime time = LocalDateTime.of(2026, 1, 15, 10, 30);
        String note = "Toller Tag!";
        Long userId = 1L;

        // System under test aufsetzen
        MoodEntry entry = new MoodEntry();
        entry.setId(42L);
        entry.setMood(mood);
        entry.setTime(time);
        entry.setNote(note);
        entry.setUserId(userId);

        // Vergleich
        assertEquals(42L, entry.getId());
        assertEquals(mood, entry.getMood());
        assertEquals(time, entry.getTime());
        assertEquals(note, entry.getNote());
        assertEquals(userId, entry.getUserId());
    }
}
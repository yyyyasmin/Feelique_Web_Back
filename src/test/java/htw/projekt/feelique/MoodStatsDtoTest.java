package htw.projekt.feelique;

import htw.projekt.feelique.rest.model.MoodStatsDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoodStatsDtoTest {

    @Test
    void testMoodStatsDtoWithConstructor() {
        // Eingabedaten
        String mood = "Glücklich";
        long count = 5L;

        // System under test aufsetzen
        MoodStatsDto dto = new MoodStatsDto(mood, count);

        // Vergleich
        assertEquals(mood, dto.getMood());
        assertEquals(count, dto.getCount());
    }

    @Test
    void testSetters() {
        // System under test aufsetzen
        MoodStatsDto dto = new MoodStatsDto();
        dto.setMood("Traurig");
        dto.setCount(3L);

        // Vergleich
        assertEquals("Traurig", dto.getMood());
        assertEquals(3L, dto.getCount());
    }
}
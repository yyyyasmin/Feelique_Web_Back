package htw.projekt.feelique;

import htw.projekt.feelique.business.repository.MoodEntryRepository;
import htw.projekt.feelique.business.repository.UserRepository;
import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.model.MoodEntry;
import htw.projekt.feelique.rest.model.MoodStatsDto;
import htw.projekt.feelique.rest.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("removal")
@SpringBootTest
public class MoodEntryServiceTest {

    @Autowired
    private MoodEntryService moodEntryService;

    @MockBean
    private MoodEntryRepository moodEntryRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("should get all moods")
    void testGetAllMoods() {
        // Test Daten
        MoodEntry m1 = new MoodEntry();
        m1.setId(1L);
        m1.setMood("happy");

        MoodEntry m2 = new MoodEntry();
        m2.setId(2L);
        m2.setMood("sad");

        doReturn(Arrays.asList(m1, m2)).when(moodEntryRepository).findAll();

        // Aufruf
        List<MoodEntry> result = moodEntryService.getAllMoods();

        // Vergleich
        assertEquals(2, result.size());
        assertEquals("happy", result.get(0).getMood());
    }

    @Test
    @DisplayName("should save mood entry")
    void testSaveMood() {
        // Test Daten
        MoodEntry entry = new MoodEntry();
        entry.setMood("excited");
        entry.setUserId(1L);

        doReturn(entry).when(moodEntryRepository).save(entry);

        // Aufruf
        MoodEntry result = moodEntryService.saveMood(entry);

        // Vergleich
        assertNotNull(result);
        assertEquals("excited", result.getMood());
        verify(moodEntryRepository).save(entry);
    }

    @Test
    @DisplayName("should delete mood by id")
    void testDeleteMood() {
        // Test Daten
        Long id = 42L;
        doReturn(true).when(moodEntryRepository).existsById(id);
        doNothing().when(moodEntryRepository).deleteById(id);

        // Aufruf
        moodEntryService.deleteMood(id);

        // Vergleich
        verify(moodEntryRepository).deleteById(id);
    }

    @Test
    @DisplayName("should throw exception when deleting non-existent mood")
    void testDeleteMoodNotFound() {
        doReturn(false).when(moodEntryRepository).existsById(999L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moodEntryService.deleteMood(999L);
        });

        assertTrue(exception.getMessage().contains("Mood not found"));
    }

    @Test
    @DisplayName("should get entries for user ordered by time")
    void testGetEntriesForUser() {
        // Test Daten
        Long userId = 1L;
        MoodEntry m1 = new MoodEntry();
        m1.setMood("happy");
        m1.setUserId(userId);

        doReturn(Arrays.asList(m1)).when(moodEntryRepository).findByUserIdOrderByTimeDesc(userId);

        // Aufruf
        List<MoodEntry> result = moodEntryService.getEntriesForUser(userId);

        // Vergleich
        assertEquals(1, result.size());
        assertEquals("happy", result.get(0).getMood());
    }

    @Test
    @DisplayName("should get entry by id")
    void testGetEntryById() {
        // Test Daten
        MoodEntry entry = new MoodEntry();
        entry.setId(5L);
        entry.setMood("calm");

        doReturn(Optional.of(entry)).when(moodEntryRepository).findById(5L);

        // Aufruf
        Optional<MoodEntry> result = moodEntryService.getEntryById(5L);

        // Vergleich
        assertTrue(result.isPresent());
        assertEquals("calm", result.get().getMood());
    }

    @Test
    @DisplayName("should update mood entry successfully")
    void testUpdateMoodEntry() {
        // Test Daten
        Long entryId = 10L;
        String token = "valid-token";
        User user = new User("user@htw-berlin.de", "hash");
        user.setId(1L);

        MoodEntry existingEntry = new MoodEntry();
        existingEntry.setId(entryId);
        existingEntry.setMood("happy");
        existingEntry.setUserId(1L);

        doReturn(user).when(userRepository).findBySessionToken(token);
        doReturn(Optional.of(existingEntry)).when(moodEntryRepository).findById(entryId);
        doReturn(existingEntry).when(moodEntryRepository).save(any(MoodEntry.class));

        // Aufruf
        MoodEntry result = moodEntryService.updateMoodEntry(
                entryId,
                "excited",
                LocalDateTime.now(),
                "Updated note",
                token
        );

        // Vergleich
        assertNotNull(result);
        assertEquals("excited", result.getMood());
        verify(moodEntryRepository).save(any(MoodEntry.class));
    }

    @Test
    @DisplayName("should throw exception when updating with invalid token")
    void testUpdateMoodEntryUnauthorized() {
        doReturn(null).when(userRepository).findBySessionToken("invalid-token");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moodEntryService.updateMoodEntry(1L, "happy", LocalDateTime.now(), "note", "invalid-token");
        });

        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("should get mood statistics for user")
    void testGetMoodStatsForUser() {
        // Test Daten
        Long userId = 1L;
        MoodEntry m1 = new MoodEntry();
        m1.setMood("happy");
        m1.setTime(LocalDateTime.of(2026, 1, 10, 10, 0));
        m1.setUserId(userId);

        MoodEntry m2 = new MoodEntry();
        m2.setMood("happy");
        m2.setTime(LocalDateTime.of(2026, 1, 11, 10, 0));
        m2.setUserId(userId);

        MoodEntry m3 = new MoodEntry();
        m3.setMood("sad");
        m3.setTime(LocalDateTime.of(2026, 1, 12, 10, 0));
        m3.setUserId(userId);

        doReturn(Arrays.asList(m1, m2, m3)).when(moodEntryRepository).findByUserIdOrderByTimeDesc(userId);

        // Aufruf
        List<MoodStatsDto> result = moodEntryService.getMoodStatsForUser(userId, null, null);

        // Vergleich
        assertEquals(2, result.size());
        assertEquals("happy", result.get(0).getMood());
        assertEquals(2, result.get(0).getCount());
        assertEquals("sad", result.get(1).getMood());
        assertEquals(1, result.get(1).getCount());
    }

    @Test
    @DisplayName("should get mood statistics for user with date range")
    void testGetMoodStatsForUserWithDateRange() {
        // Test Daten
        Long userId = 1L;
        LocalDate from = LocalDate.of(2026, 1, 11);
        LocalDate to = LocalDate.of(2026, 1, 12);

        MoodEntry m1 = new MoodEntry();
        m1.setMood("happy");
        m1.setTime(LocalDateTime.of(2026, 1, 10, 10, 0));

        MoodEntry m2 = new MoodEntry();
        m2.setMood("sad");
        m2.setTime(LocalDateTime.of(2026, 1, 11, 10, 0));

        MoodEntry m3 = new MoodEntry();
        m3.setMood("excited");
        m3.setTime(LocalDateTime.of(2026, 1, 12, 10, 0));

        doReturn(Arrays.asList(m1, m2, m3)).when(moodEntryRepository).findByUserIdOrderByTimeDesc(userId);

        // Aufruf
        List<MoodStatsDto> result = moodEntryService.getMoodStatsForUser(userId, from, to);

        // Vergleich
        assertEquals(2, result.size());
    }
}
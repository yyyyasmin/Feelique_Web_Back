package htw.projekt.feelique.business.service;

import htw.projekt.feelique.business.repository.MoodEntryRepository;
import htw.projekt.feelique.business.repository.UserRepository;
import htw.projekt.feelique.rest.model.MoodEntry;
import htw.projekt.feelique.rest.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import htw.projekt.feelique.rest.model.MoodStatsDto;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;
    private final UserRepository userRepository;

    public MoodEntryService(MoodEntryRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<MoodEntry> getAllMoods() {
        return (List<MoodEntry>) repository.findAll();
    }

    public MoodEntry saveMood(MoodEntry moodEntry) {
        return repository.save(moodEntry);
    }

    public void deleteMood(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mood not found with id: " + id);
        }
        repository.deleteById(id);
    }

    public List<MoodEntry> getEntriesForUser(Long userId) {
        return repository.findByUserIdOrderByTimeDesc(userId);
    }

    public Optional<MoodEntry> getEntryById(Long id) {
        return repository.findById(id);
    }

    public MoodEntry updateMoodEntry(Long id, String mood, LocalDateTime time, String note, String sessionToken) {
        // Korrektur: findBySessionToken liefert bei dir direkt User oder null
        User user = userRepository.findBySessionToken(sessionToken);
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        MoodEntry entry = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        // Sicherstellen, dass der Eintrag dem User gehört
        if (!entry.getUserId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        // Aktualisieren
        entry.setMood(mood);
        entry.setTime(time);
        entry.setNote(note);

        return repository.save(entry);
    }

    public List<MoodStatsDto> getMoodStatsForUser(Long userId, LocalDate from, LocalDate to) {
        // Alle Einträge des Users holen
        List<MoodEntry> entries = getEntriesForUser(userId);

        // Optionalen Zeitraum anwenden
        if (from != null || to != null) {
            entries = entries.stream().filter(e -> {
                LocalDate date = e.getTime().toLocalDate();
                if (from != null && date.isBefore(from)) {
                    return false;
                }
                if (to != null && date.isAfter(to)) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }

        // Gruppieren nach Mood und zählen
        Map<String, Long> grouped = entries.stream()
                .collect(Collectors.groupingBy(MoodEntry::getMood, Collectors.counting()));

        // In DTO-Liste umwandeln
        return grouped.entrySet().stream()
                .map(e -> new MoodStatsDto(e.getKey(), e.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount())) // absteigend nach Häufigkeit
                .collect(Collectors.toList());
    }
}
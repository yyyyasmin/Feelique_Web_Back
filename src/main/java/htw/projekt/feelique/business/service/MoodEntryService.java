package htw.projekt.feelique.business.service;

import htw.projekt.feelique.business.repository.MoodEntryRepository;
import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {

    private final MoodEntryRepository repository;

    public MoodEntryService(MoodEntryRepository repository) {
        this.repository = repository;
    }

    public List<MoodEntry> getAllMoods() {
        return (List<MoodEntry>) repository.findAll();
    }

    public MoodEntry saveMood(MoodEntry moodEntry) {
        return repository.save(moodEntry);
    }

    public MoodEntry getMoodById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mood not found with id: " + id));
    }

    public void deleteMood(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mood not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // NEU: Einträge für einen bestimmten User
    public List<MoodEntry> getEntriesForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    // NEU: Eintrag per ID holen (für DELETE-Check)
    public Optional<MoodEntry> getEntryById(Long id) {
        return repository.findById(id);
    }
}
package htw.projekt.feelique.business.service;

import htw.projekt.feelique.business.repository.MoodEntryRepository;
import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}
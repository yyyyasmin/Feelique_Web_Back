package htw.projekt.feelique.rest.controller;

import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MoodEntryController {

    private final MoodEntryService moodEntryService;

    public MoodEntryController(MoodEntryService moodEntryService) {
        this.moodEntryService = moodEntryService;
    }

    @GetMapping("/moods")
    public List<MoodEntry> getAllMoods() {
        return moodEntryService.getAllMoods();
    }

    @PostMapping("/moods")
    public MoodEntry createMood(@RequestBody MoodEntry moodEntry) {
        return moodEntryService.saveMood(moodEntry);
    }

    @GetMapping("/moods/{id}")
    public MoodEntry getMoodById(@PathVariable Long id) {
        return moodEntryService.getMoodById(id);
    }

    @DeleteMapping("/moods/{id}")
    public void deleteMood(@PathVariable Long id) {
        moodEntryService.deleteMood(id);
    }
}
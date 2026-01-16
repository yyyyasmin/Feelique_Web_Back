package htw.projekt.feelique.rest.controller;

import htw.projekt.feelique.business.service.MoodEntryService;
import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MoodEntryController {

    private final MoodEntryService moodEntryService;

    public MoodEntryController(MoodEntryService moodEntryService) {
        this.moodEntryService = moodEntryService;
    }


    @GetMapping("/moods")
    public List<MoodEntry> getMoodEntries() {
        return moodEntryService.getAllMoods();
    }
}
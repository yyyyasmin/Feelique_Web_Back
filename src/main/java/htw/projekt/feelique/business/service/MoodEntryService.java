package htw.projekt.feelique.business.service;

import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MoodEntryService {

    public List<MoodEntry> getAllMoods(){
        return List.of(
                new MoodEntry("Glücklich", LocalDateTime.now()),
                new MoodEntry("Traurig", LocalDateTime.now()),
                new MoodEntry("Aufgeregt", LocalDateTime.now()),
                new MoodEntry("Sauer", LocalDateTime.now()),
                new MoodEntry("Entspannt", LocalDateTime.now() ),
                new MoodEntry("Müde", LocalDateTime.now()),
                new MoodEntry("Neutral", LocalDateTime.now()),
                new MoodEntry("Gelangweilt", LocalDateTime.now()),
                new MoodEntry("Schlecht", LocalDateTime.now()),
                new MoodEntry("Gestresst", LocalDateTime.now())
        );
    }
}

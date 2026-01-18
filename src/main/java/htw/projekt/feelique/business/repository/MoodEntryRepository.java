package htw.projekt.feelique.business.repository;

import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoodEntryRepository extends CrudRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserIdOrderByTimeDesc(Long userId);
}
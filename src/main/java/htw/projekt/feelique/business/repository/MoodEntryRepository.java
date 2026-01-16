package htw.projekt.feelique.business.repository;

import htw.projekt.feelique.rest.model.MoodEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodEntryRepository extends CrudRepository<MoodEntry, Long> {
    // Standard-Methoden wie save, findById, findAll, deleteById sind automatisch verfügbar
}
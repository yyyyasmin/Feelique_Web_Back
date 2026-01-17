package htw.projekt.feelique.business.repository;

import htw.projekt.feelique.rest.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    User findBySessionToken(String sessionToken);
}
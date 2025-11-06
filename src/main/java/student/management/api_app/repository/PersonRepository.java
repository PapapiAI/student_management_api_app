package student.management.api_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import student.management.api_app.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByFullNameContainingIgnoreCase(String keyword);

    Optional<Person> findByPhone(String phone);
    boolean existsByPhone(String phone);

    List<Person> findByContactEmailIgnoreCase(String email);
}

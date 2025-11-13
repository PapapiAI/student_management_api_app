package student.management.api_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import student.management.api_app.model.Person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends
        JpaRepository<Person, UUID>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByPhone(String phone);
    boolean existsByPhone(String phone);

    Page<Person> findByIdIn(Collection<UUID> ids, Pageable pageable);
}

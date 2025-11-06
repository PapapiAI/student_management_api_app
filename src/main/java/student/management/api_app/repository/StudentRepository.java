package student.management.api_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import student.management.api_app.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentCode(String studentCode);

    List<Student> findByEnrollmentYear(Integer enrollmentYear);
    List<Student> findByPerson_FullNameContainingIgnoreCase(String keyword);
}

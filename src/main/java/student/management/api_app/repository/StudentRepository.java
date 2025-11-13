package student.management.api_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import student.management.api_app.dto.student.EnrollmentStatDTO;
import student.management.api_app.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository
        extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentCode(String studentCode);

    Page<Student> findByEnrollmentYear(Integer enrollmentYear, Pageable pageable);

    @Query("""
        SELECT s FROM Student s
        JOIN s.person p
        WHERE p.phone = :phone
    """)
    Optional<Student> findByPhone(@Param("phone") String phone);

    @Query("""
        SELECT new student.management.api_app.dto.student.EnrollmentStatDTO(
            s.enrollmentYear,
            COUNT(s)
        )
        FROM Student s
        GROUP BY s.enrollmentYear
    """)
    List<EnrollmentStatDTO> countStudentsGroupedByYear();
}

package student.management.api_app.repository;

import org.springframework.stereotype.Repository;
import student.management.api_app.model.Student;

import java.util.*;

@Repository
public class StudentRepository {
    private final Map<UUID, Student> db = new HashMap<>();

    public List<Student> findAll() {
        return new ArrayList<>(db.values());
    }

    public Optional<Student> findById(UUID id) {
        return Optional.ofNullable(db.get(id));
    }

    public Student save(Student s) {
        if (s.getId() == null) s.setId(UUID.randomUUID());
        db.put(s.getId(), s);
        return s;
    }

    public void deleteById(UUID id) {
        db.remove(id);
    }
}

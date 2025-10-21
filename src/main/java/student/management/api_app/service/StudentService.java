package student.management.api_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import student.management.api_app.dto.student.StudentCreateRequest;
import student.management.api_app.dto.student.StudentResponse;
import student.management.api_app.dto.student.StudentUpdateRequest;
import student.management.api_app.model.Student;
import student.management.api_app.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repo;

    // === Helper: map Student → StudentResponse ===
    private StudentResponse toResponse(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getFullName(),
                s.getAge(),
                s.getEmail(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.isAdult()
        );
    }

    public List<StudentResponse> getAllStudents() {
//        List<Student> students = repo.findAll();
//        List<StudentResponse> responses = new ArrayList<>();
//
//        for (Student student : students) {
//            StudentResponse res = toResponse(student);
//            responses.add(res);
//        }
//
//        return responses;

        // Using Stream to handle Collection data
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse getStudentById(UUID id) {
//        Optional<Student> studentOtp = repo.findById(id);
//
//        if (studentOtp.isPresent()) {
//            return toResponse(studentOtp.get());
//        } else {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Not found student with id: " + id);
//        }

        return repo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not found student with id: " + id));
    }

    public StudentResponse createStudent(StudentCreateRequest req) {
        // Business rule validation
        if (req.fullName() == null || req.fullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
        }
        if (req.age() == null || req.age() < 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be greater than 16");
        }
        if (req.email() == null || !req.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is invalid");
        }

        Student student = Student.builder()
                .fullName(req.fullName())
                .age(req.age())
                .email(req.email())
                .build();

        student.onCreate();
        return toResponse(repo.save(student));
    }

    public StudentResponse updateStudent(UUID id, StudentUpdateRequest req) {
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
        }
        if (req.getAge() == null || req.getAge() < 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be greater than 16");
        }

        Student student = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not found student with id: " + id));

        student.setFullName(req.getFullName());
        student.setAge(req.getAge());
        student.onUpdate();

        return toResponse(repo.save(student));
    }

    public void deleteStudent(UUID id) {
        if (repo.findById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not found student with id: " + id);
        }

        repo.deleteById(id);
    }
}

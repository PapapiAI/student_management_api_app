package student.management.api_app.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import student.management.api_app.dto.ApiResponse;
import student.management.api_app.dto.student.StudentCreateRequest;
import student.management.api_app.dto.student.StudentResponse;
import student.management.api_app.dto.student.StudentUpdateRequest;
import student.management.api_app.service.StudentService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/students")
@Tag(name = "Student Management", description = "Student Management API")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @Operation(summary = "Get student list",
            description = "Bài thực hành buổi 3: Thiết kế API `/api/v1/students` in-memory")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudents() {
        List<StudentResponse> students = service.getAllStudents();
        ApiResponse<List<StudentResponse>> studentList = ApiResponse.<List<StudentResponse>>builder()
                .success(true)
                .data(students)
                .error(null)
                .build(); // khởi tạo đối tượng sau khi đã set các giá trị
        return ResponseEntity.ok(studentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@RequestBody StudentCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStudent(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @PathVariable UUID id,
            @RequestBody StudentUpdateRequest req
    ) {
        return ResponseEntity.ok(service.updateStudent(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

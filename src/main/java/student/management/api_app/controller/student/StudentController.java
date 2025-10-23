package student.management.api_app.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import student.management.api_app.dto.ApiResponse;
import student.management.api_app.dto.student.StudentCreateRequest;
import student.management.api_app.dto.student.StudentResponse;
import student.management.api_app.dto.student.StudentUpdateRequest;
import student.management.api_app.service.StudentService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/students")
@Tag(name = "Student Management", description = "Student Management API")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService service;

    @Operation(
            summary = "Get student list",
            description = "Bài thực hành buổi 3: Thiết kế API `GET /api/v1/students` in-memory",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Success"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudents() {
        List<StudentResponse> list = service.getAllStudents();

        ApiResponse<List<StudentResponse>> response = ApiResponse.<List<StudentResponse>>builder()
                .success(true)
                .data(list)
                .error(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get student by id",
            description = "Bài thực hành buổi 3: Thiết kế API `GET /api/v1/students/id` in-memory",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Found"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.ApiError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable UUID id) {
        StudentResponse student = service.getStudentById(id);

        ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                .success(true).data(student).build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create student",
            description = "Bài thực hành buổi 3: Thiết kế API `POST /api/v1/students` in-memory",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Created"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(schema = @Schema(implementation = ApiResponse.ApiError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(@RequestBody StudentCreateRequest req) {
        StudentResponse created = service.createStudent(req);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponse.<StudentResponse>builder()
                .success(true).data(created).build());
    }

    @Operation(
            summary = "Create student",
            description = "Bài thực hành buổi 3: Thiết kế API `PUT /api/v1/students/id` in-memory",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Updated"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.ApiError.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(schema = @Schema(implementation = ApiResponse.ApiError.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable UUID id,
            @RequestBody StudentUpdateRequest req
    ) {
        StudentResponse updated = service.updateStudent(id, req);

        return ResponseEntity.ok(ApiResponse.<StudentResponse>builder()
                .success(true).data(updated).build());
    }

    @Operation(
            summary = "Delete student",
            description = "Bài thực hành buổi 3: Thiết kế API `DELETE /api/v1/students/id` in-memory",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "Deleted"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(schema = @Schema(implementation = ApiResponse.ApiError.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
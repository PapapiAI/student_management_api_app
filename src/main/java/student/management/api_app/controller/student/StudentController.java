package student.management.api_app.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import student.management.api_app.dto.AppResponse;
import student.management.api_app.dto.student.*;
import student.management.api_app.service.impl.StudentService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/students")
@Tag(name = "Student Management", description = "Student Management API")
@RequiredArgsConstructor
public class StudentController {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final StudentService service;

    @Operation(
            summary = "Get all students",
            description = "Lấy danh sách tất cả học viên",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping
    public ResponseEntity<AppResponse<List<StudentListItemResponse>>> getAll() {
        return ResponseEntity.ok(AppResponse.<List<StudentListItemResponse>>builder()
                .success(true)
                .data(service.getAll())
                .error(null)
                .build());
    }

    @Operation(
            summary = "Search students by person name",
            description = "Tìm student theo tên Person (không phân biệt hoa/thường). " +
                    "Trả về rỗng nếu keyword trống",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping("/search")
    public ResponseEntity<AppResponse<List<StudentListItemResponse>>> searchByPersonName(
            @RequestParam("name") String keyword) {
        return ResponseEntity.ok(AppResponse.<List<StudentListItemResponse>>builder()
                .success(true)
                .data(service.searchByPersonName(keyword))
                .build());
    }

    @Operation(
            summary = "List students by enrollment year",
            description = "Lọc student theo enrollmentYear.",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping("/by-year")
    public ResponseEntity<AppResponse<List<StudentListItemResponse>>> listByEnrollmentYear(
            @RequestParam("year") Integer year) {
        return ResponseEntity.ok(AppResponse.<List<StudentListItemResponse>>builder()
                .success(true)
                .data(service.listByEnrollmentYear(year))
                .build());
    }

    @Operation(
            summary = "Get student by id",
            description = "Lấy chi tiết học viên theo ID (trùng với personId)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Student not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<StudentDetailResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(AppResponse.<StudentDetailResponse>builder()
                .success(true)
                .data(service.getById(id))
                .build());
    }

    @Operation(
            summary = "Get student by studentCode",
            description = "Tìm student theo studentCode",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Student not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @GetMapping("/by-student-code")
    public ResponseEntity<AppResponse<StudentDetailResponse>> getByStudentCode(
            @RequestParam("student-code") String studentCode) {
        return ResponseEntity.ok(AppResponse.<StudentDetailResponse>builder()
                    .success(true)
                    .data(service.getByStudentCode(studentCode))
                .build());
    }

    @Operation(
            summary = "Create student with new person (composite create)",
            description = """
                    Tạo mới Person và Student trong cùng một transaction.
                    Trả về 201 Created và Location header. Body gồm:
                    - person: thông tin cá nhân (fullName bắt buộc, phone unique nếu có)
                    - student: thông tin sinh viên (studentCode bắt buộc, unique)
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "409", description = "Unique constraint",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<AppResponse<StudentDetailResponse>> create(
            @RequestBody StudentCreateRequest req) {
        StudentDetailResponse created = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.personDetail().id())
                .toUri();

        return ResponseEntity.created(location).body(AppResponse.<StudentDetailResponse>builder()
                .success(true)
                .data(created)
                .build());
    }

    @Operation(
            summary = "Create student from existing person",
            description = "Tạo Student cho Person đã tồn tại. Trả về 201 Created và location header",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "404", description = "Person not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Student already exists for person / studentCode duplicate",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
            }
    )
    @PostMapping("/by-person")
    public ResponseEntity<AppResponse<StudentDetailResponse>> createFromExistingPerson(
            @RequestBody StudentCreateFromPersonRequest req) {
        StudentDetailResponse created = service.createFromExistingPerson(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(apiPrefix + "/students/{id}")
                .buildAndExpand(created.personDetail().id())
                .toUri();

        return ResponseEntity.created(location).body(AppResponse.<StudentDetailResponse>builder()
                .success(true)
                .data(created)
                .build());
    }

    @Operation(
            summary = "Patch student by ID",
            description = "Cập nhật từng phần cho học viên. " +
                    "Truyền field cần cập nhật, gửi null để xóa field (nếu cho phép)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "404", description = "Student not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Unique constraint violated in DB",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AppResponse<StudentDetailResponse>> patch(
            @PathVariable UUID id,
            @RequestBody StudentPatchRequest req) {
        return ResponseEntity.ok(AppResponse.<StudentDetailResponse>builder()
                .success(true)
                .data(service.patch(id, req))
                .build());
    }

    @Operation(
            summary = "Delete student by ID",
            description = "Xóa học viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "404", description = "Student not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
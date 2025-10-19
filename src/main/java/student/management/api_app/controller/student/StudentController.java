package student.management.api_app.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import student.management.api_app.dto.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/students")
@Tag(name = "Student Management", description = "Student Management API")
public class StudentController {

    @Operation(summary = "Get empty student list",
            description = "Bài thực hành buổi 2: Thiết kế API `/api/v1/students` trả danh sách rỗng")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Object>>> getStudents() {
        ApiResponse<List<Object>> studentList = ApiResponse.<List<Object>>builder()
                .success(true)
                .data(List.of())
                .error(null)
                .build(); // khởi tạo đối tượng sau khi đã set các giá trị
        return ResponseEntity.ok(studentList);
    }
}

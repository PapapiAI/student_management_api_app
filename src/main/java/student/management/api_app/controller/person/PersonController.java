package student.management.api_app.controller.person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import student.management.api_app.dto.AppResponse;
import student.management.api_app.dto.page.PageResponse;
import student.management.api_app.dto.person.*;
import student.management.api_app.service.IPersonService;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/persons")
@RequiredArgsConstructor
public class PersonController {
    private final IPersonService service;

    @Operation(
            summary = "Get all persons with pagination",
            description = "Lấy danh sách tất cả person có phân trang",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping
    public ResponseEntity<AppResponse<PageResponse<PersonListItemResponse>>> getAll(
            @ParameterObject
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(AppResponse.success(service.getAll(pageable)));
    }

    @Operation(
            summary = "Search persons by attribute",
            description = """
                    Tìm kiếm person với nhiều điều kiện tùy chọn:
                    - name: chứa trong fullName (ignore case)
                    - phone: đúng với phone (sau normalize)
                    - email: chứa trong contactEmail
                    - dobFrom / dobTo: khoảng năm sinh
                    \nHỗ trợ phân trang & sort theo mọi field hợp lệ
                    """,
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping("/search")
    public ResponseEntity<AppResponse<PageResponse<PersonListItemResponse>>> search(
            @ParameterObject PersonSearchRequest req,
            @PageableDefault(size = 5, sort = {"createdAt", "fullName"}, direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(AppResponse.success(service.search(req, pageable)));
    }

    @Operation(
            summary = "List persons by IDs with pagination",
            description = "Nhận danh sách UUID qua body (POST) để tránh giới hạn độ dài URL." +
                    "Trả về danh sách rỗng nếu danh sách UUID trống có phân trang",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @PostMapping("/list-by-ids") // POST body để không giới hạn độ dài URL
    public ResponseEntity<AppResponse<PageResponse<PersonListItemResponse>>> listByIds(
            @RequestBody Collection<UUID> ids,
            @PageableDefault(size = 5, sort = {"createdAt", "fullName"}, direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(AppResponse.success(service.listByIds(ids, pageable)));
    }

    @Operation(
            summary = "Get person by ID",
            description = "Lấy chi tiết person theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "404", description = "Person not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<PersonDetailResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(AppResponse.<PersonDetailResponse>builder()
                .success(true)
                .data(service.getById(id))
                .build());
    }

    @Operation(
            summary = "Get person by phone",
            description = "Tìm person theo số điện thoại",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Phone is required",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)) ),
                    @ApiResponse(responseCode = "404", description = "Person not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @GetMapping("/by-phone")
    public ResponseEntity<AppResponse<PersonDetailResponse>> getByPhone(
            @RequestParam("phone") String phone) {
        return ResponseEntity.ok(AppResponse.<PersonDetailResponse>builder()
                .success(true)
                .data(service.getByPhone(phone))
                .build());
    }

    @Operation(
            summary = "Create a new person",
            description = "Tạo person mới. Trả về 201 Created và Location header",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Full name is required",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Phone is existed (unique constraint)",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<AppResponse<PersonDetailResponse>> create(
            @RequestBody PersonCreateRequest req) {
        PersonDetailResponse created = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(AppResponse.<PersonDetailResponse>builder()
                .success(true)
                .data(created)
                .build());
    }

    @Operation(
            summary = "Patch person by ID",
            description = "Cập nhật từng phần cho person. " +
                    "Truyền field cần cập nhật; gửi null để xóa field (nếu cho phép)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "404", description = "Person not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Unique constraint violated in DB",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AppResponse<PersonDetailResponse>> patch(
            @PathVariable UUID id,
            @RequestBody PersonPatchRequest req) {
        return ResponseEntity.ok(AppResponse.<PersonDetailResponse>builder()
                .success(true)
                .data(service.patch(id, req))
                .build());
    }

    @Operation(
            summary = "Delete person by ID",
            description = "Xóa person theo ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "404", description = "Person not found",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}

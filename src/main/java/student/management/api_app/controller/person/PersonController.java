package student.management.api_app.controller.person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import student.management.api_app.dto.AppResponse;
import student.management.api_app.dto.person.PersonCreateRequest;
import student.management.api_app.dto.person.PersonDetailResponse;
import student.management.api_app.dto.person.PersonListItemResponse;
import student.management.api_app.dto.person.PersonPatchRequest;
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
            summary = "Get all persons",
            description = "Lấy danh sách tất cả person",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping
    public ResponseEntity<AppResponse<List<PersonListItemResponse>>> getAll() {
        return ResponseEntity.ok(AppResponse.<List<PersonListItemResponse>>builder()
                .success(true)
                .data(service.getAll())
                .build());
    }

    @Operation(
            summary = "Search persons by name",
            description = "Tìm person theo tên (không phân biệt hoa/thường). " +
                    "Trả về danh sách rỗng nếu keyword trống",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @GetMapping("/search")
    public ResponseEntity<AppResponse<List<PersonListItemResponse>>> searchByName(
            @RequestParam("name") String keyword) {
        return ResponseEntity.ok(AppResponse.<List<PersonListItemResponse>>builder()
                .success(true)
                .data(service.searchByName(keyword))
                .build());
    }


    @Operation(
            summary = "Search persons by contact email",
            description = "Tìm person theo email liên hệ (không phân biệt hoa/thường)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Email is required",
                            content = @Content(schema = @Schema(
                                    implementation = AppResponse.AppError.class)))
            }
    )
    @GetMapping("/search-by-email")
    public ResponseEntity<AppResponse<List<PersonListItemResponse>>> searchByContactEmail(
            @RequestParam("email") String email) {
        return ResponseEntity.ok(AppResponse.<List<PersonListItemResponse>>builder()
                .success(true)
                .data(service.searchByContactEmail(email))
                .build());
    }

    @Operation(
            summary = "List persons by IDs",
            description = "Nhận danh sách UUID qua body (POST) để tránh giới hạn độ dài URL." +
                    "Trả về danh sách rỗng nếu danh sách UUID trống",
            responses = @ApiResponse(responseCode = "200", description = "Success")
    )
    @PostMapping("/list-by-ids") // POST body để không giới hạn độ dài URL
    public ResponseEntity<AppResponse<List<PersonListItemResponse>>> listByIds(
            @RequestBody Collection<UUID> ids) {
        return ResponseEntity.ok(AppResponse.<List<PersonListItemResponse>>builder()
                .success(true)
                .data(service.listByIds(ids))
                .build());
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

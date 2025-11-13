package student.management.api_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {
    boolean success;
    T data; // List/Object/Null
    AppError error;

    @Builder.Default
    Instant timestamp = Instant.now();

    // No need for ApiError if using the RFC 7807 standard (Problem Details)
    @Value
    @Builder
    public static class AppError {
        String code;
        String message;
        String path;
    }

    // Factory method (static helper methods)
    public static <T> AppResponse<T> success(T data) {
        return AppResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> AppResponse<T> error(String code, String message, String path) {
        return AppResponse.<T>builder()
                .success(false)
                .error(AppError.builder()
                        .code(code)
                        .message(message)
                        .path(path)
                        .build())
                .build();
    }
}

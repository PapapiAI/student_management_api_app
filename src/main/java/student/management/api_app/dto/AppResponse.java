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
}

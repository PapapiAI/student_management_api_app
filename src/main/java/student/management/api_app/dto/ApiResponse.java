package student.management.api_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    boolean success;
    T data;
    ApiError error;

    @Builder.Default
    Instant timestamp = Instant.now();

    // No need for ApiError if using the RFC 7807 standard (Problem Details)
    @Value
    @Builder
    public static class ApiError {
        String code;
        String message;
        String path;
    }
}

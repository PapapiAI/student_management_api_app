package student.management.api_app.dto.student;

import java.time.Instant;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String fullName,
        Integer age,
        String email,

        Instant createdAt,
        Instant updatedAt,

        Boolean adult // computed field
) {}

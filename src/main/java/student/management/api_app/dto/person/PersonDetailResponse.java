package student.management.api_app.dto.person;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PersonDetailResponse(
        UUID id,
        String fullName,
        LocalDate dob,
        String phone,
        String contactEmail,
        String address,
        boolean isAdult,
        Instant createdAt,
        Instant updatedAt
) {
}

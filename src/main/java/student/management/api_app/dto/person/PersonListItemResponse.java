package student.management.api_app.dto.person;

import java.util.UUID;

public record PersonListItemResponse(
        UUID id,
        String fullName,
        String contactEmail,
        Boolean isAdult
) {
}

package student.management.api_app.dto.person;

import java.time.LocalDate;

public record PersonCreateRequest(
        String fullName,
        LocalDate dob,
        String phone,
        String contactEmail,
        String address
) {
}

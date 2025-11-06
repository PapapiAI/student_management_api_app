package student.management.api_app.dto.student;

import student.management.api_app.dto.person.PersonDetailResponse;

import java.time.Instant;

public record StudentDetailResponse(
        PersonDetailResponse personDetail,

        String studentCode,
        Integer enrollmentYear,
        Instant createdAt,
        Instant updatedAt
) {
}

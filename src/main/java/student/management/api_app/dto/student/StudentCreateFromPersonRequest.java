package student.management.api_app.dto.student;

import java.util.UUID;

public record StudentCreateFromPersonRequest(
        UUID personId,
        String studentCode,
        Integer enrollmentYear
) {
}

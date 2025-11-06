package student.management.api_app.dto.student;

public record StudentCreateOnlyRequest(
        String studentCode,
        Integer enrollmentYear
) {
}

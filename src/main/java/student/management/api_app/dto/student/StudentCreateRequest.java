package student.management.api_app.dto.student;

public record StudentCreateRequest(
        String fullName,
        Integer age,
        String email
) {}

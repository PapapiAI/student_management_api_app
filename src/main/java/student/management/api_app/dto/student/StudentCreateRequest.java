package student.management.api_app.dto.student;

import student.management.api_app.dto.person.PersonCreateRequest;

public record StudentCreateRequest(
        PersonCreateRequest person,
        StudentCreateOnlyRequest student
) {
}

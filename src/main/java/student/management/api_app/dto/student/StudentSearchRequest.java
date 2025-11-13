package student.management.api_app.dto.student;

import student.management.api_app.dto.person.PersonSearchRequest;

public record StudentSearchRequest(

        PersonSearchRequest person,

        String studentCode,
        Integer enrollmentYearFrom,
        Integer enrollmentYearTo
) {}

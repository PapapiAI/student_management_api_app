package student.management.api_app.dto.person;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record PersonSearchRequest(
        String name,
        String phone,
        String email,
        String address,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dobFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dobTo
) {
}

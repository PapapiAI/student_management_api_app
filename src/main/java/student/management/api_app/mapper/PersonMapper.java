package student.management.api_app.mapper;

import org.springframework.stereotype.Component;
import student.management.api_app.dto.person.PersonDetailResponse;
import student.management.api_app.dto.person.PersonListItemResponse;
import student.management.api_app.model.Person;
import student.management.api_app.util.AgeCalculator;

@Component
public class PersonMapper {
    public PersonListItemResponse toListItemResponse(Person p) {
        return new PersonListItemResponse(
                p.getId(),
                p.getFullName(),
                p.getContactEmail(),
                AgeCalculator.isAdult(p.getDob())
        );
    }

    public PersonDetailResponse toDetailResponse(Person p) {
        return new PersonDetailResponse(
                p.getId(),
                p.getFullName(),
                p.getDob(),
                p.getPhone(),
                p.getContactEmail(),
                p.getAddress(),
                AgeCalculator.isAdult(p.getDob()),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}

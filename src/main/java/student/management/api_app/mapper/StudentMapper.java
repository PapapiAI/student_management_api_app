package student.management.api_app.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import student.management.api_app.dto.student.StudentDetailResponse;
import student.management.api_app.dto.student.StudentListItemResponse;
import student.management.api_app.model.Person;
import student.management.api_app.model.Student;
import student.management.api_app.util.AgeCalculator;

@RequiredArgsConstructor
@Component
public class StudentMapper {
    private final PersonMapper personMapper;

    public StudentDetailResponse toDetailResponse(Student s) {
        return new StudentDetailResponse(
                personMapper.toDetailResponse(s.getPerson()),
                s.getStudentCode(),
                s.getEnrollmentYear(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    public StudentListItemResponse toListItemResponse(Student s) {
        Person p = s.getPerson();

        return new StudentListItemResponse(
                s.getId(),
                s.getStudentCode(),
                s.getEnrollmentYear(),
                p.getFullName(),
                p.getContactEmail(),
                AgeCalculator.isAdult(p.getDob())
        );
    }
}

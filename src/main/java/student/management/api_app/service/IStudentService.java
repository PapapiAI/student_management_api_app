package student.management.api_app.service;

import student.management.api_app.dto.student.*;

import java.util.List;
import java.util.UUID;

public interface IStudentService {
    List<StudentListItemResponse> getAll();
    List<StudentListItemResponse> searchByPersonName(String keyword);
    List<StudentListItemResponse> listByEnrollmentYear(Integer year);

    StudentDetailResponse getById(UUID id);
    StudentDetailResponse getByStudentCode(String studentCode);

    StudentDetailResponse create(StudentCreateRequest req);
    StudentDetailResponse createFromExistingPerson(StudentCreateFromPersonRequest req);
    StudentDetailResponse patch(UUID id, StudentPatchRequest req);
    void deleteById(UUID id);
}

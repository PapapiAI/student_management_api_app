package student.management.api_app.service;

import student.management.api_app.dto.person.PersonCreateRequest;
import student.management.api_app.dto.person.PersonDetailResponse;
import student.management.api_app.dto.person.PersonListItemResponse;
import student.management.api_app.dto.person.PersonPatchRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPersonService {
    List<PersonListItemResponse> getAll();
    List<PersonListItemResponse> searchByName(String keyword);
    List<PersonListItemResponse> searchByContactEmail(String email);
    List<PersonListItemResponse> listByIds(Collection<UUID> ids);

    PersonDetailResponse getById(UUID id);
    PersonDetailResponse getByPhone(String phone);

    PersonDetailResponse create(PersonCreateRequest req);
    PersonDetailResponse patch(UUID id, PersonPatchRequest req);
    void deleteById(UUID id);
}

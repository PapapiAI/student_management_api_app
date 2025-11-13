package student.management.api_app.service;

import org.springframework.data.domain.Pageable;
import student.management.api_app.dto.page.PageResponse;
import student.management.api_app.dto.person.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPersonService {
    PageResponse<PersonListItemResponse> getAll(Pageable pageable);
    PageResponse<PersonListItemResponse> search(PersonSearchRequest req, Pageable pageable);

    PageResponse<PersonListItemResponse> listByIds(Collection<UUID> ids, Pageable pageable);

    PersonDetailResponse getById(UUID id);
    PersonDetailResponse getByPhone(String phone);

    PersonDetailResponse create(PersonCreateRequest req);
    PersonDetailResponse patch(UUID id, PersonPatchRequest req);
    void deleteById(UUID id);
}

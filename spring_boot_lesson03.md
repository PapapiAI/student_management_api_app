# Spring Boot – Buổi 3: Mô hình MVC & CRUD Student

## 1) Mô hình MVC trong Spring Boot

### 1.1 Khái niệm tổng quan

> **MVC (Model–View–Controller)** là mô hình thiết kế phần mềm giúp tách biệt rõ ràng giữa các tầng trong ứng dụng, nhằm:
> * Dễ bảo trì, mở rộng và kiểm thử code
> * Cho phép nhiều DEV làm việc song song trên các phần khác nhau (Model, Controller, View)
> * Tăng tính tái sử dụng và giảm sự phụ thuộc giữa các thành phần

#### Trong ứng dụng Spring Boot, MVC được áp dụng mạnh mẽ trong việc xây dựng `RESTful API`, với cấu trúc phân tách như sau:


| Thành phần     | Vai trò chính                                                                                        | Trong Spring Boot                              | Ví dụ minh họa                                       |
|----------------|------------------------------------------------------------------------------------------------------|------------------------------------------------|------------------------------------------------------|
| **Model**      | Đại diện cho dữ liệu và logic nghiệp vụ                                                              | `Entity` / `Domain Object` / `DTO` / `Service` | `Student` entity biểu diễn sinh viên trong database  |
| **View**       | Hiển thị dữ liệu cho người dùng (giao diện)<br/>Trong API, View thường là JSON response thay vì HTML | `REST API` trả JSON (Spring Web)               | {"id": 1, "name": "John Doe", "age": 20}             |
| **Controller** | Xử lý request, điều phối luồng dữ liệu giữa View và Model                                            | `@RestController`                              | `StudentController` quản lý API `/api/v1/students`   |

> * Trong ứng dụng `Web MVC` truyền thống (Spring MVC + Thymeleaf), View là file .html
> * Nhưng trong `Spring Boot REST API`, View chính là `JSON` dữ liệu trả về cho client hoặc frontend

### 1.2 Flow xử lý MVC

```
Client → Controller → Service → Repository → Database
                     ↑          ↓
                     Response ← Data
```

> * `Controller` là điểm vào của mọi request. Nó nhận dữ liệu từ người dùng (qua request body, params, path, …), sau đó gọi `Service` để xử lý nghiệp vụ
> * `Service` (nằm trong tầng Model) thực hiện logic chính, có thể gọi đến `Repository` để truy xuất dữ liệu
> * `Repository` là nơi làm việc trực tiếp với Database, thường sử dụng `Spring Data JPA` (JpaRepository, CrudRepository, …)
> * Kết quả xử lý được trả ngược lại qua `Controller`, rồi gửi về client dưới dạng `JSON response` (View)

---

## 2) Dependency Injection (Spring IoC)

### 2.1 IoC (Inversion of Control)

* Thay vì lập trình viên tự khởi tạo và quản lý object, **Spring Container** làm việc đó.
* Các object (bean) được tạo, cấu hình, và quản lý vòng đời bởi Spring.

### 2.2 Dependency Injection

* Là quá trình Spring **tự động truyền (inject)** các dependency cần thiết vào class.

Ví dụ:

```java
@Service
public class StudentService {
    private final StudentRepository repo;

    @Autowired  // Tự động inject repository vào service
    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }
}
```

**Các cách inject phổ biến:**

1. **Constructor Injection (được khuyên dùng)** – An toàn và dễ test.
2. **Field Injection (@Autowired trực tiếp trên field)** – Ngắn gọn nhưng khó test.
3. **Setter Injection** – Phù hợp khi dependency tùy chọn.

---

## 3) Thực hành: CRUD cho Student (In-Memory DB)

### 3.1 Cấu trúc dự án

```
src/main/java/student/management/api_app/
 ├── controller/StudentController.java
 ├── service/StudentService.java
 ├── repository/StudentRepository.java
 ├── model/Student.java
 ├── dto/StudentRequest.java
 └── dto/StudentResponse.java
```

---

### 3.2 Model và DTO

```java
// model/Student.java
package com.example.studentapp.model;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private UUID id;
    private String fullname;
    private String email;
    private Integer age;
}
```

```java
// dto/StudentRequest.java
package com.example.studentapp.dto;

import jakarta.validation.constraints.*;

public record StudentRequest(
    @NotBlank String fullname,
    @Email String email,
    @Min(10) @Max(80) Integer age
) {}
```

```java
// dto/StudentResponse.java
package com.example.studentapp.dto;

import java.util.UUID;

public record StudentResponse(UUID id, String fullname, String email, Integer age) {}
```

---

### 3.3 Repository Layer (In-memory)

```java
package com.example.studentapp.repository;

import com.example.studentapp.model.Student;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class StudentRepository {
    private final Map<UUID, Student> db = new HashMap<>();

    public List<Student> findAll() {
        return new ArrayList<>(db.values());
    }

    public Optional<Student> findById(UUID id) {
        return Optional.ofNullable(db.get(id));
    }

    public Student save(Student s) {
        if (s.getId() == null) s.setId(UUID.randomUUID());
        db.put(s.getId(), s);
        return s;
    }

    public void deleteById(UUID id) {
        db.remove(id);
    }
}
```

---

### 3.4 Service Layer

```java
package com.example.studentapp.service;

import com.example.studentapp.dto.*;
import com.example.studentapp.model.Student;
import com.example.studentapp.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repo;

    public List<StudentResponse> getAll() {
        return repo.findAll().stream()
                .map(s -> new StudentResponse(s.getId(), s.getFullname(), s.getEmail(), s.getAge()))
                .toList();
    }

    public StudentResponse create(StudentRequest req) {
        Student s = Student.builder()
                .fullname(req.fullname())
                .email(req.email())
                .age(req.age())
                .build();
        return map(repo.save(s));
    }

    public Optional<StudentResponse> getById(UUID id) {
        return repo.findById(id).map(this::map);
    }

    public Optional<StudentResponse> update(UUID id, StudentRequest req) {
        return repo.findById(id).map(s -> {
            s.setFullname(req.fullname());
            s.setEmail(req.email());
            s.setAge(req.age());
            return map(repo.save(s));
        });
    }

    public boolean delete(UUID id) {
        if (repo.findById(id).isPresent()) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private StudentResponse map(Student s) {
        return new StudentResponse(s.getId(), s.getFullname(), s.getEmail(), s.getAge());
    }
}
```

---

### 3.5 Controller Layer

```java
package com.example.studentapp.controller;

import com.example.studentapp.dto.*;
import com.example.studentapp.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService service;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found"));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody StudentRequest req) {
        return service.update(id, req)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }
}
```

---

## 4) Chạy thử & Kiểm tra

### 4.1 Swagger UI

* Truy cập: `http://localhost:8080/swagger-ui.html`
* Kiểm tra các endpoint CRUD:

    * `GET /api/v1/students`
    * `GET /api/v1/students/{id}`
    * `POST /api/v1/students`
    * `PUT /api/v1/students/{id}`
    * `DELETE /api/v1/students/{id}`

### 4.2 Postman

* Tạo collection “Student CRUD” và test toàn bộ API.
* Thử tạo mới, cập nhật, xóa và đọc danh sách.

---

## 5) Bài tập về nhà (Homework)

1. Thêm xử lý lỗi validation khi thiếu trường hoặc sai định dạng email.
2. Thêm field `createdAt` và `updatedAt` cho Student.
3. (Tuỳ chọn) Dùng `List<Student>` trong repository và sắp xếp theo `fullname`.
4. Chuẩn bị cho buổi 4: Validation nâng cao & Exception Handling.

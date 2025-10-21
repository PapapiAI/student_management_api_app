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

> * Thay vì lập trình viên tự khởi tạo và quản lý object, **Spring Container** làm việc đó.
> * Các object (bean) được tạo, cấu hình, và quản lý vòng đời bởi Spring.

### 2.2 Dependency Injection

#### 2.2.1 Khái niệm

> * Dependency Injection (DI) là cơ chế Spring tự động truyền (inject) các đối tượng mà một class phụ thuộc, thay vì lập trình viên phải tự khởi tạo (new) thủ công.
> * DI giúp Spring chịu trách nhiệm quản lý vòng đời (lifecycle) và mối quan hệ phụ thuộc giữa các bean trong ứng dụng

**Ví dụ**
  * `StudentService` cần sử dụng `StudentRepository`
  * Thay vì phải viết `StudentRepository repo = new StudentRepository();` 

→ Spring sẽ tự tạo instance và inject nó vào StudentService

#### 2.2.2 Cơ chế Spring thực hiện DI

> Spring Boot có cơ chế IoC Container (Inversion of Control Container) để quản lý toàn bộ bean (đối tượng được Spring tạo ra và quản lý)
> * Khi ứng dụng khởi động, Spring quét các class có annotation như `@Component`, `@Service`, `@Repository`, `@Controller`
> * Các bean được tạo và lưu trong `ApplicationContext`
> * Khi một bean cần phụ thuộc vào bean khác, Spring **tự động inject** phụ thuộc đó

```java
@Service
public class StudentService {
  private final StudentRepository repo;

  // Constructor Injection (nên dùng)
  @Autowired
  public StudentService(StudentRepository repo) {
    this.repo = repo;
  }

  public List<Student> getAllStudents() {
    return repo.findAll();
  }
}
```

#### 2.2.3 Các cách Inject phổ biến

| Kiểu Injection        | Cách viết                                                              | Ưu điểm                                         | Hạn chế                                       |
|-----------------------|------------------------------------------------------------------------|-------------------------------------------------|-----------------------------------------------|
| Constructor Injection | Inject qua hàm khởi tạo (`@Autowired` hoặc `@RequiredArgsConstructor`) | An toàn (`final field`), dễ test, không bị null | Cách được khuyên dùng                         |
| Field Injection       | `@Autowired` trực tiếp trên thuộc tính                                 | Ngắn gọn, dễ viết                               | Khó test (không thể inject mock), không final |
| Setter Injection      | Inject qua setter method                                               | Linh hoạt, dùng khi dependency tùy chọn         | Dễ bị bỏ qua nếu không gọi setter             |


**Ví dụ từng cách**

**1. Constructor Injection (nên dùng)**

```java
@Service
@RequiredArgsConstructor // Lombok tự tạo constructor cho final fields
public class StudentService {
    private final StudentRepository repo;
}
```

> * Annotation `@RequiredArgsConstructor`: của Lombok, giúp tự tạo constructor cho final fields
> * Annotation `@Service`: của Spring, giúp Spring tạo Bean cho tầng Service
> * Annotation `@Repository`: của Spring, giúp Spring tạo Bean cho tầng Repository
> * Annotation `@Component`: của Spring, đánh dấu một class bất kỳ là Spring Bean (tổng quát)  

**2. Field Injection**

```java
@Service
public class StudentService {
  @Autowired
  private StudentRepository repo;
}
```

**3. Setter Injection**

```java
@Service
public class StudentService {
    private StudentRepository repo;

    @Autowired
    public void setRepo(StudentRepository repo) {
        this.repo = repo;
    }
}
```

#### 2.2.3 Thuật ngữ cần nhớ đối với Dependency Injection

| Thuật ngữ               | Ý nghĩa                                             |
|-------------------------|-----------------------------------------------------|
| `Bean`                  | Là object được Spring quản lý                       |
| `IoC Container`         | Là nơi Spring lưu và quản lý các bean               |
| `Dependency Injection`  | Là hành động Spring “tiêm” bean cần thiết vào class |
| `@Autowired`            | Annotation để Spring biết cần inject dependency     |
| `Constructor Injection` | Cách inject tốt nhất, nên dùng mặc định             |

---

## 3) Thực hành: CRUD cho Student (In-Memory DB)

### 3.1 Cấu trúc dự án

```
src/main/java/student/management/api_app/
 ├── controller/student/StudentController.java
 ├── service/StudentService.java
 ├── repository/StudentRepository.java
 ├── model/Student.java
 ├── dto/student/StudentCreateRequest.java
 ├── dto/student/StudentUpdateRequest.java
 └── dto/student/StudentResponse.java
```

---

### 3.2 Model và DTO

#### 3.2.1 Model: `Student`

> Vì là In-Memory, `Student` tạm thời là Domain Model thuần (chưa cần @Entity)
> * Các thuộc tính:
>   * `id` kiểu UUID, tự động tạo trong Service (in-memory)
>   * `fullName`, `dob`, `email`, `createdAt`, `updatedAt`
> * Nghiệp vụ:
>   * Tuổi học viên phải từ 16t trở lên
>   * Lưu lại thời điểm tạo mới và update học viên 

```java
// model/Student.java

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
  UUID id;
  String fullName;
  Integer age;
  String email;

  Instant createdAt;
  Instant updatedAt;

  // === Business helpers ===
  public boolean isAdult() {
    return age != null && age >= 18;
  }

  public void onCreate() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  public void onUpdate() {
    this.updatedAt = Instant.now();
  }
}
```

> * Annotation `@Getter`: của Lombok, tự động tạo getter methods cho tất cả thuộc tính
> * Annotation `@Setter`: tương tự `@Getter` → tự tạo setter methods
> * Annotation `@NoArgsConstructor`: của Lombok, tự tạo constructor không tham số
> * Annotation `@AllArgsConstructor`: tương tự `@NoArgsConstructor` → tự tạo constructor có đủ tham số cho tất cả field
> * `Instant`: Kiểu dữ liệu thời gian, luôn ở `UTC` (múi giờ 0 - ứng với chữ Z ở cuối) → KHÔNG bị phụ thuộc vào múi giờ hệ thống

#### 3.2.2 DTO

> * `DTO (Data Transfer Object)` là lớp dùng để nhận dữ liệu từ client (`request`) hoặc trả dữ liệu về client (`response`)
> * Nó **KHÔNG** chứa logic nghiệp vụ, chỉ có nhiệm vụ vận chuyển dữ liệu qua lại giữa client ↔ server

**1. `StudentResponse` → trả về cho client**

```java
// dto/student/StudentResponse.java

public record StudentResponse(
        UUID id,
        String fullName,
        Integer age,
        String email,

        Instant createdAt,
        Instant updatedAt,

        Boolean adult // computed field
) {}
```

**Sử dụng `record` thay cho `class`**

> * `record` là một kiểu đặc biệt của `class` được thiết kế để lưu trữ dữ liệu **bất biến** (immutable data carrier)
> * Tự đông tạo:
>   * `private final` fields
>   * `constructor`
>   * `getters`
>   * `equals()` / `hashCode()` / `toString()`
> * Vì là immutable → **KHÔNG** có setters
> * `record` chỉ nên chứa dữ liệu, không nên xử lý logic nghiệp vụ

**Nên dùng `record` ở đâu**

> Nếu `class` chỉ dùng để mang dữ liệu (data-only), hãy dùng `record` thay cho `class` để code ngắn gọn, rõ ràng và an toàn hơn

| Nơi                           | Dùng `record` | Lý do                                            |
|-------------------------------|---------------|--------------------------------------------------|
| `DTO` (`Request`/`Response`)  | Nên           | Dữ liệu chỉ mang tính truyền tải, không thay đổi |
| `Entity` hoặc `mutable class` | Không nên     | Vì không thể setter hoặc cập nhật                |

**2. `StudentCreateRequest` → POST /students**

```java
// dto/student/StudentCreateRequest.java

public record StudentCreateRequest(
        String fullName,
        Integer age,
        String email
) {}
```

**3. `StudentUpdateRequest` → PUT /students/{id}**

* Có thể chuyển sang sử dụng `record`
* Ở đây sử dụng `class` kết hợp với `lombok` để so sánh cách dùng

```java
// dto/student/StudentUpdateRequest.java

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentUpdateRequest {
  String fullName;
  Integer age;
}
```

---

### 3.3 Repository Layer (In-memory)

> * `Repository` là lớp trung gian giữa tầng `Service` và nguồn dữ liệu (`database`).
    Nó chịu trách nhiệm lưu trữ, truy vấn, cập nhật, xóa dữ liệu
> * `Service` chỉ cần “gọi `Repository` để lấy hoặc lưu dữ liệu”, không cần biết dữ liệu nằm ở đâu (trong DB thật, hay chỉ là bộ nhớ tạm)

```java
// repository/StudentRepository.java

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

**`@Repository` là annotation của Spring framework, giúp Spring:**

> * Tự động tạo `Bean` cho class này (để có thể `@Autowired` ở `Service`)
> * Đánh dấu class này thuộc tầng `Repository`
> * Tự động quản lý ngoại lệ (`Exception Translation`) khi làm việc với DB thật (`JPA`/`Hibernate`)

**Dùng `Optional` trong `findById(UUID id)`**

> * `findById(UUID id)` Tìm học viên theo `id` → kết quả có thể `null` nếu `id` không đúng
> * Dùng kiểu `Optional` để tránh `NullPointerException` → nếu không tìm thấy thì `Optional.ofNullable(db.get(id))` trả về `Optional.empty()`
>   * Phương thức static `Optional.ofNullable(value)` → tạo ra một `Optional` từ giá trị có thể `null`
>     * Nếu `value != null` → trả về `Optional` chứa giá trị đó
>     * Nếu `value == null` → trả về `Optional.empty()` (một Optional rỗng, không có giá trị)

---

### 3.4 Service Layer

```java
// service/StudentService.java

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository repo;

  // === Helper: map Student → StudentResponse ===
  private StudentResponse toResponse(Student s) {
    return new StudentResponse(
            s.getId(),
            s.getFullName(),
            s.getAge(),
            s.getEmail(),
            s.getCreatedAt(),
            s.getUpdatedAt(),
            s.isAdult()
    );
  }

  public List<StudentResponse> getAllStudents() {
    // Using Stream to handle Collection data
    return repo.findAll().stream()
            .map(this::toResponse)
            .toList();
  }

  public StudentResponse getStudentById(UUID id) {
    return repo.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not found student with id: " + id));
  }

  public StudentResponse createStudent(StudentCreateRequest req) {
    // Business rule validation
    if (req.fullName() == null || req.fullName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
    }
    if (req.age() == null || req.age() < 16) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be greater than 16");
    }
    if (req.email() == null || !req.email().contains("@")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is invalid");
    }

    Student student = Student.builder()
            .fullName(req.fullName())
            .age(req.age())
            .email(req.email())
            .build();

    student.onCreate();
    return toResponse(repo.save(student));
  }

  public StudentResponse updateStudent(UUID id, StudentUpdateRequest req) {
    if (req.getFullName() == null || req.getFullName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
    }
    if (req.getAge() == null || req.getAge() < 16) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "age must be greater than 16");
    }

    Student student = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not found student with id: " + id));

    student.setFullName(req.getFullName());
    student.setAge(req.getAge());
    student.onUpdate();

    return toResponse(repo.save(student));
  }

  public void deleteStudent(UUID id) {
    if (repo.findById(id).isEmpty()) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Not found student with id: " + id);
    }

    repo.deleteById(id);
  }
}
```

> * `Stream` là một dòng dữ liệu tuần tự cho phép xử lý từng phần tử bằng các thao tác như `map`, `filter`, `sorted`, `forEach`, `collect`, ...
>   * `students.stream()` → phương thức stream() của Collection chuyển `List<Student>` thành `Stream<Student>`
>   * `.map(this::toResponse)` → phương thức `map()` chuyển đổi từng phần tử trong `stream` từ kiểu `Student` sang kiểu `StudentResponse`
>     * `this::toResponse` là method reference, tương đương với `student -> this.toResponse(student)`
>   * `.toList()` → thu thập (collect) các phần tử trong `stream` thành một `List<StudentResponse>` mới
> * Các phương thức của Optional
>   * `map(this::toResponse)`
>     * Nếu `value == null` → trả về `Optional.empty()`
>     * Nếu `value != null` → chuyển `student` tìm thấy sang `StudentResponse`
>   * `orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found student with id: " + id))`
>     * Nếu `value != null` → trả về giá trị đó (đã được map thành StudentResponse)
>     * Nếu `value == null` → ném ResponseStatusException với mã 404 và message
>     * Cần truyền vào một `Supplier`
>       * `Supplier` là một `lambda` không nhận tham số nào, nhưng trả về một giá trị kiểu `T` (ở đây là `ResponseStatusException`)

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

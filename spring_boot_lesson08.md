# Spring Boot – Buổi 8: Entity Relationships & Performance

## 1) Tổng quan về quan hệ giữa các Entity

Trong ứng dụng Spring Data JPA, quan hệ giữa các bảng trong database được ánh xạ thành quan hệ giữa các entity tương ứng. 
Đây là nền tảng quan trọng để xây dựng mô hình dữ liệu đúng chuẩn và truy vấn hiệu quả.

Các loại quan hệ chính:

* One-to-One (`1–1`)
* One-to-Many / Many-to-One (`1–N` / `N–1`)
* Many-to-Many (`N–N`)

---

## 2) One-to-Many & Many-to-One Mapping

### 2.1 Many-to-One (N–1)

* Định nghĩa
  * Many-to-One: Nhiều bản ghi ở bảng A liên kết tới 1 bản ghi ở bảng B
  * Ở level entity: Nhiều entity A tham chiếu tới cùng một entity B
* Nghiệp vụ thực tế: `Student` (N) - `Major` (1) là quan hệ Many-to-One
  * Một chuyên ngành có nhiều học viên theo học
  * Mỗi học viên chỉ thuộc 1 chuyên ngành chính

### 2.1.1 Tạo bảng majors và Alter bảng students

```sql
-- db/migration/V1.0.2__create_table_majors.sql

SET search_path TO app;

CREATE TABLE IF NOT EXISTS majors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_code VARCHAR(50) NOT NULL UNIQUE,
    major_name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

DROP TRIGGER IF EXISTS trg_majors_updated_at ON majors;
CREATE TRIGGER trg_majors_updated_at
BEFORE UPDATE ON majors
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
```

* Alter bảng `students` để thêm quan hệ với bảng `majors`
  * Bên N (`students`) sẽ chứa FK (`major_id`) tham chiếu sang bên 1 (`majors`)
  * Quan hệ này là `optional` vì có thể có học viên dự thính (chưa phân chuyên ngành)

```sql
-- db/migration/V1.0.3__alter_students_add_major_relation.sql

SET search_path TO app;

ALTER TABLE students
ADD COLUMN IF NOT EXISTS major_id UUID;

ALTER TABLE students
ADD CONSTRAINT fk_students_major
FOREIGN KEY (major_id)
REFERENCES majors(id);
```

### 2.1.2 Mapping Many-to-One trong JPA

* Entity bên 1 – `Major`

```java

```




















### 2.2 One-to-Many (1–N)

```java
@Entity
public class Classroom {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();
}
```

### **Ghi nhớ quan trọng**

| Thuộc tính                  | Ý nghĩa                                                   |
| --------------------------- | --------------------------------------------------------- |
| `mappedBy`                  | Chỉ ra entity đang giữ khóa ngoại (side sở hữu quan hệ)   |
| `cascade = CascadeType.ALL` | Tự động áp dụng insert/update/delete cho entity con       |
| `fetch = FetchType.LAZY`    | Tải dữ liệu khi cần thiết → tránh tải toàn bộ cây quan hệ |

---

## **3) Many-to-Many Mapping**

Ví dụ: Student đăng ký nhiều Course, Course có nhiều Student.

### **Bảng trung gian (join table)**

```java
@Entity
public class Student {
    @Id @GeneratedValue
    private Long id;

    private String fullName;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}
```

```java
@Entity
public class Course {
    @Id @GeneratedValue
    private Long id;

    private String courseName;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

**Nhận xét:** thay vì dùng `@ManyToMany` trực tiếp → hệ thống lớn thường dùng **entity trung gian** `Enrollment` để dễ thêm dữ liệu bổ sung (score, status, createdAt…).

---

## **4) Join Operations trong JPA/Hibernate**

### **4.1 Join mặc định qua quan hệ**

```java
List<Student> students = classroom.getStudents();
```

Hibernate tự join theo mapping.

### **4.2 Join trong JPQL**

```java
SELECT s FROM Student s JOIN s.classroom c WHERE c.name = :name
```

### **4.3 Fetch Join (quan trọng!)**

Dùng để tải dữ liệu liên quan ngay trong 1 query.

```java
SELECT c FROM Classroom c
LEFT JOIN FETCH c.students
WHERE c.id = :id
```

→ Giải pháp phổ biến để tránh **N+1 Query Problem**.

---

## **5) Cascade trong JPA**

### **5.1 Cascade Types**

| Cascade | Ý nghĩa                                             |
| ------- | --------------------------------------------------- |
| ALL     | Áp dụng toàn bộ hành động (persist, merge, remove…) |
| PERSIST | Tự động lưu entity con khi lưu entity cha           |
| REMOVE  | Xóa entity con khi xóa entity cha                   |
| MERGE   | Cập nhật entity con khi merge cha                   |
| REFRESH | Refresh entity theo dữ liệu DB                      |
| DETACH  | Tách entity khỏi Persistence Context                |

### **Best Practice**

* `CascadeType.ALL` chỉ nên dùng cho quan hệ **parent → child** (1–N).
* Tuyệt đối không dùng cascade cho Many-to-Many nếu không nắm rõ → dễ xóa lẫn nhau.

---

## **6) Lazy vs Eager Loading**

### **6.1 LAZY (mặc định, nên dùng)**

* Dữ liệu liên quan chỉ được tải khi thực sự gọi.
* Tránh tải lượng dữ liệu lớn không cần thiết.

### **6.2 EAGER (hạn chế dùng)**

* Luôn JOIN và tải toàn bộ dữ liệu liên quan.
* Có thể dẫn đến query rất nặng.

```java
@ManyToOne(fetch = FetchType.EAGER)
```

### **So sánh**

| Loading | Ưu điểm                   | Nhược điểm                                              |
| ------- | ------------------------- | ------------------------------------------------------- |
| LAZY    | Hiệu năng tốt, ít dữ liệu | Có thể gặp LazyInitializationException nếu session đóng |
| EAGER   | Dễ dùng                   | Dễ gây N+1, join thừa, truy vấn chậm                    |

---

## **7) N+1 Query Problem & Giải Pháp**

### **7.1 N+1 Problem là gì?**

Ví dụ: load 1 Classroom → Hibernate tự sinh **N truy vấn** để load N học sinh.

```sql
SELECT * FROM classroom WHERE id = 1;
SELECT * FROM student WHERE classroom_id = 1;
```

→ Nếu có 100 classrooms → 101 query → quá chậm.

### **7.2 Cách phát hiện**

* Bật log Hibernate SQL
* Dùng công cụ như p6spy, datasource-proxy

### **7.3 Giải pháp xử lý**

#### **✔ Fetch Join** (tốt nhất)

```java
@Query("SELECT c FROM Classroom c LEFT JOIN FETCH c.students WHERE c.id = :id")
Classroom findWithStudents(Long id);
```

#### **✔ EntityGraph**

```java
@EntityGraph(attributePaths = {"students"})
Classroom findById(Long id);
```

#### **✔ Batch Size (Hibernate)**

```java
@BatchSize(size = 20)
private List<Student> students;
```

Trong `application.properties`:

```properties
hibernate.default_batch_fetch_size=20
```

### **7.4 Khi nào dùng giải pháp nào?**

| Tình huống                  | Giải pháp               |
| --------------------------- | ----------------------- |
| API cần load đầy đủ dữ liệu | Fetch Join              |
| Query phức tạp nhiều bảng   | EntityGraph             |
| Danh sách lớn               | Batch Fetching          |
| Quan hệ Many-to-Many        | BatchSize + EntityGraph |

---

## **8) Tổng kết bài học**

Trong buổi học này, bạn đã nắm được:

* Cách khai báo **One-to-Many**, **Many-to-Many** quan hệ giữa các entity.
* Cách dùng **Join**, **Fetch Join**, **EntityGraph** để tối ưu truy vấn.
* Hiểu bản chất của **Lazy/Eager loading** và cách sử dụng đúng.
* Nhận diện và xử lý **N+1 Problem** – lỗi phổ biến gây chậm hệ thống.

Kiến thức này là nền tảng quan trọng để xây dựng các hệ thống lớn, tối ưu hiệu năng và thiết kế domain model đúng chuẩn.

---

Bạn muốn mình tiếp tục soạn **Buổi 9 – Query DSL / Criteria API / Specification Nâng Cao** không?

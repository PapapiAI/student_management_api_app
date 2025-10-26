# Spring Boot – Buổi 5: ORM & Hibernate Introduction

## 1) ORM là gì?

### 1.1 Khái niệm ORM

> **ORM (Object Relational Mapping)** là kỹ thuật ánh xạ (chuyển đổi dữ liệu) giữa `object trong Java` và `bảng trong database` một cách tự động
>
> Thay vì viết SQL thủ công (như trong JDBC thuần), ORM cho phép thao tác bằng `object`

Ví dụ:

* Với JDBC thuần phải viết:

```java
String sql = "INSERT INTO students(full_name, email, age) VALUES (?, ?, ?)";
```

* Với ORM chỉ cần:

```java
Student s = new Student("Nguyen Van A", 20, "vana@gmail.com");
session.persist(s);
```

→ Hibernate sẽ tự động tạo câu SQL tương ứng:

```sql
INSERT INTO students(full_name, age, email) VALUES ('Nguyen Van A', 20, 'vana@gmail.com');
```

### 1.2 Lợi ích của ORM

| Lợi ích                | Mô tả                                                                  |
|------------------------|------------------------------------------------------------------------|
| Giảm code SQL thủ công | Không cần viết nhiều câu lệnh JDBC                                     |
| Tự ánh xạ Object–Table | Entity ↔ Table, Field ↔ Column                                         |
| Dễ bảo trì             | Khi cấu trúc bảng thay đổi, chỉ cần cập nhật Entity                    |
| An toàn hơn            | Tránh SQL injection                                                    |
| Đa hệ CSDL             | Có thể đổi DB dễ dàng (MySQL, PostgreSQL, H2, …) mà không cần đổi code |
| Tích hợp transaction   | Hibernate tự quản lý commit/rollback                                   |

---

## 2) Giới thiệu Hibernate

### 2.1 Hibernate là gì

> Hibernate là một `ORM framework` phổ biến nhất trong hệ sinh thái Java, gồm các tính năng:
> * Quản lý ánh xạ Java class ↔ table trong DB
> * Tự động sinh SQL
> * Quản lý Transaction
> Nó thực hiện ánh xạ giữa Java class và bảng CSDL, và cung cấp API để thực hiện CRUD.

### 2.2 Các thành phần chính của Hibernate

| Thành phần         | Vai trò                                                          |
|--------------------|------------------------------------------------------------------|
| **Configuration**  | Nạp file cấu hình (hibernate.cfg.xml) và khởi tạo SessionFactory |
| **SessionFactory** | Tạo và quản lý các Session                                       |
| **Session**        | Đại diện cho kết nối (connection) đến DB, dùng để CRUD entity    |
| **Transaction**    | Đảm bảo tính toàn vẹn của dữ liệu khi thực hiện nhiều thao tác   |

### 2.3 Chu trình hoạt động Hibernate

```
SessionFactory → Session → Transaction → CRUD → Commit/Rollback → Close Session
```

---

## 3) Entity Mapping cơ bản

### 3.1 Tạo Entity Student

```java
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    private Integer age;

    @Column(unique = true)
    private String email;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

> * `@Entity`: đánh dấu class là entity sẽ ánh xạ tới table trong DB.
> * `@Table`: đặt tên bảng.
> * `@Id`: xác định khóa chính.
> * `@GeneratedValue`: chỉ định cơ chế sinh khóa tự động.
> * `@PrePersist`, `@PreUpdate`: tự động gán thời gian tạo/cập nhật.

### 3.2 File cấu hình Hibernate (`hibernate.cfg.xml`)

```xml
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings -->
        <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
        <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/student_management</property>
        <property name="hibernate.connection.username">app_user</property>
        <property name="hibernate.connection.password">123456</property>

        <!-- SQL dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>

        <!-- Show SQL -->
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <!-- Update schema automatically -->
        <property name="hibernate.hbm2ddl.auto">update</property>

        <!-- Annotated classes -->
        <mapping class="student.management.model.Student"/>
    </session-factory>
</hibernate-configuration>
```

> * `hibernate.hbm2ddl.auto`: tự động cập nhật schema (`validate`, `update`, `create`, `create-drop`).
> * Trong sản phẩm thật, nên dùng Flyway để quản lý migration thay vì auto-update.

---

## 4) Session & Transaction trong Hibernate

### 4.1 Cấu trúc cơ bản

```java
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class HibernateDemo {
    public static void main(String[] args) {
        Configuration config = new Configuration().configure();
        try (SessionFactory factory = config.buildSessionFactory();
             Session session = factory.openSession()) {

            Transaction tx = session.beginTransaction();

            Student student = Student.builder()
                    .fullName("Nguyen Van A")
                    .age(22)
                    .email("vana@example.com")
                    .build();

            session.persist(student);
            tx.commit();

            System.out.println("Saved student with id: " + student.getId());
        }
    }
}
```

### 4.2 Giải thích

| Thành phần       | Vai trò                                     |
| ---------------- | ------------------------------------------- |
| `SessionFactory` | Sinh `Session` mới khi cần giao tiếp với DB |
| `Session`        | Làm việc với các Entity, thực hiện CRUD     |
| `Transaction`    | Quản lý commit/rollback cho các thao tác DB |

---

## 5) So sánh JDBC vs ORM

| Tiêu chí               | JDBC                      | ORM (Hibernate)              |
| ---------------------- | ------------------------- | ---------------------------- |
| Mức độ trừu tượng      | Thấp (code SQL trực tiếp) | Cao (làm việc qua Entity)    |
| Viết SQL thủ công      | Có                        | Không cần, Hibernate tự sinh |
| Mapping Object ↔ Table | Phải tự làm               | Tự động qua annotation       |
| Transaction            | Tự quản lý                | Hibernate tự động            |
| Dễ mở rộng             | Khó                       | Dễ, tích hợp tốt với Spring  |

---

## 6) Thực hành: Lưu Entity Student vào DB

### 6.1 Các bước

1. Tạo class `Student` (như trên).
2. Cấu hình `hibernate.cfg.xml`.
3. Viết `HibernateDemo.java` để tạo `Session`, mở `Transaction`, lưu dữ liệu.
4. Kiểm tra dữ liệu trong bảng `student` trên PostgreSQL.

**Kết quả mong đợi:**

```
Hibernate:
    insert into student (age, created_at, email, full_name, updated_at, id)
    values (?, ?, ?, ?, ?, ?)
✅ Student persisted successfully!
```

---

## 7) Bài tập mở rộng

* Thêm trường `address` và `gender` vào entity `Student`.
* Thực hành truy vấn `session.createQuery("from Student where age >= 18", Student.class).list()`.
* So sánh lại code JDBC (Buổi 4) và Hibernate (Buổi 5).

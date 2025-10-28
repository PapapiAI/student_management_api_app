# Spring Boot – Buổi 5: ORM & Hibernate Introduction

## 1) ORM là gì?

### 1.1 Khái niệm ORM

> **ORM (Object Relational Mapping)** là kỹ thuật ánh xạ (chuyển đổi dữ liệu) giữa `object trong Java` và `bảng trong database` một cách tự động
>
> Thay vì viết SQL thủ công (như trong JDBC thuần), ORM cho phép thao tác bằng `object`

Ví dụ:

* Với JDBC thuần phải viết:

```java
public Student save(String fullName, String email, Integer age) {
    String sql = "INSERT INTO students(full_name, email, age) VALUES (?, ?, ?)";
}
```

* Với ORM chỉ cần:

```java
public Student save(String fullName, String email, Integer age) {
    Student s = new Student("Nguyen Van A", 20, "vana@gmail.com");
    session.persist(s);    
}
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

### 2.2 Các thành phần chính của Hibernate

| Thành phần       | Vai trò                                                            |
|------------------|--------------------------------------------------------------------|
| `Configuration`  | Nạp file cấu hình `hibernate.cfg.xml` và khởi tạo `SessionFactory` |
| `SessionFactory` | Tạo và quản lý các `Session`                                       |
| `Session`        | Đại diện cho kết nối (connection) đến DB, dùng để `CRUD entity`    |
| `Transaction`    | Quản lý `commit` / `rollback`                                      |
| `Entity`         | Class ánh xạ với bảng                                              |

### 2.3 Luồng hoạt động Hibernate

```
Hibernate config → SessionFactory → Session → Transaction → CRUD → Commit/Rollback → Close Session
```

---

## 3) Entity Mapping cơ bản

### 3.1 Thêm dependencies cho Hibernate

```xml
<!-- Hibernate (ORM) -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.6.1.Final</version>
</dependency>
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

### 3.2 Cấu hình `hibernate.cfg.xml`

```xml
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- JDBC -->
        <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
        <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/jdbc_demo?currentSchema=app</property>
        <property name="hibernate.connection.username">postgres</property>
        <property name="hibernate.connection.password">123456@root</property>

        <!-- Show SQL -->
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>

        <!-- Auto DDL: none -->
        <property name="hibernate.hbm2ddl.auto">none</property>

        <!-- Declare entity -->
        <mapping class="demo.jdbc.model.orm.StudentEntity"/>
    </session-factory>
</hibernate-configuration>
```

**Lưu ý:** 
* Hành vi Hibernate thực thi Auto DDL:
  * `validate`: hibernate sẽ so sánh shema giữa entity và DB, nếu khác → báo lỗi
  * `update`: hibernate tự sửa bảng ở DB cho khớp entity (ALTER TABLE) → chỉ nên dùng trong môi trường Dev
  * `none`: hibernate không tự ý sửa shema DB
* Thẻ `<mapping class="demo.jdbc.model.orm.StudentEntity"/>` cần khai báo chính xác tên và source root của entity

### 3.3 Thêm cột updated_at cho DB

* Chạy script để thêm cột updated_at

```sql
ALTER TABLE app.students
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
```

* Chạy script để tạo trigger tự động gán updated_at mỗi lần thực hiện `UPDATE`

```sql
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_updated_at
BEFORE UPDATE ON app.students
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
```

### 3.3 Tạo model `StudentEntity`

```java
// demo/jdbc/model/orm/StudentEntity.java

@Entity
@Table(name = "students", schema = "app")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "age")
    private int age;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    // Getter/Setter
    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
```

#### Giải thích Annotation 

> * `@Entity`: đánh dấu class là entity sẽ ánh xạ tới table trong DB
> * `@Table`: khai báo tên bảng và schema
> * `@Id`: xác định cột khóa chính
> * `@GeneratedValue(strategy = GenerationType.UUID)`: cấu hình để hibernate sinh ID tự động
> * `@Column`: khai báo cho cột `name`, `nullable`, `unique`, `length`, ...
>   * `insertable = false`: hibernate không đưa created_at, updated_at vào câu SQL INSERT
>     * `insert into app.students (id, full_name, email, age) values (?, ?, ?, ?)`
>   * `updatable = false`: hibernate không đưa created_at, updated_at vào câu SQL UPDATE
> * `@PrePersist`, `@PreUpdate`: hibernate tự động gán thời gian tạo/cập nhật
>   * Thông thường nên để DB tự lo gán thời gian tạo/cập nhật (đối với UPDATE thì dùng trigger)
>   * Khi update bằng tool khác không phải hibernate thì vẫn an toàn, không lo sót dữ liệu vì DB đã tự lo  

#### Bài tập 1: Viết model `StudentEntity` tương ứng với bảng students

```java
// demo/jdbc/model/orm/StudentEntity.java

// Hãy khai báo Annotation cần thiết
public class StudentEntity {

    // Hãy khai báo và cấu hình cho các cột 

    // Hãy viết các Getter/Setter cần thiết
}
```

### 3.4 File cấu hình Hibernate (`hibernate.cfg.xml`)

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

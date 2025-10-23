# Spring Boot – Buổi 4: JDBC Fundamentals

## 1) JDBC Overview

### 1.1 JDBC là gì?

> * **JDBC (Java Database Connectivity)** là chuẩn API của Java để ứng dụng Java làm việc với DB
> * Hoạt động qua **JDBC Driver** (PostgreSQL: `org.postgresql.Driver`)

#### Thành phần chính của JDBC:

| Thành phần                        | Vai trò                               |
|-----------------------------------|---------------------------------------|
| `DriverManager`                   | Quản lý các driver DB và mở kết nối   |
| `Connection`                      | Đại diện cho kết nối đang mở đến DB   |
| `Statement` / `PreparedStatement` | Gửi câu lệnh SQL đến DB               |
| `ResultSet`                       | Kết quả trả về từ câu truy vấn SELECT |


### 1.2 Luồng cơ bản (`JDBC lifecycle`)

```
Application → DriverManager → Connection → Statement → ResultSet → (map to DTO) → Application
```

1. Load driver (tự động khi có driver trong classpath)
2. Lấy `Connection` từ `DriverManager.getConnection(url, user, pass)`
3. Tạo `PreparedStatement` với SQL có tham số `?`
4. Gán tham số → `executeQuery()` (SELECT) hoặc `executeUpdate()` (INSERT/UPDATE/DELETE).
5. Duyệt `ResultSet`, map từng dòng → đối tượng Java.
6. Đóng tài nguyên (try‑with‑resources).

---

## 2) PreparedStatement & ResultSet Handling

### 2.1 PreparedStatement

> `PreparedStatement` giúp gửi câu SQL có tham số → tránh SQL Injection

**Ví dụ:**

```java
String sql = "SELECT id, full_name, email, age FROM student WHERE age >= ?";
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, 18);
    try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("full_name") + " - " + rs.getString("email"));
        }
    }
}
```

> * `?` là placeholder cho tham số.
> * Dùng `stmt.setInt`, `stmt.setString` để gán giá trị.
> * Kết quả trả về nằm trong `ResultSet`.

### 2.2 ResultSet

> `ResultSet` hoạt động như một con trỏ, duyệt từng hàng dữ liệu từ DB.

| Phương thức            | Mô tả                                                    |
| ---------------------- | -------------------------------------------------------- |
| `rs.next()`            | Di chuyển đến hàng kế tiếp (trả `false` khi hết dữ liệu) |
| `rs.getInt(column)`    | Lấy giá trị int từ cột                                   |
| `rs.getString(column)` | Lấy giá trị String                                       |
| `rs.getDate(column)`   | Lấy giá trị ngày tháng                                   |

**Ví dụ đọc dữ liệu:**

```java
while (rs.next()) {
    UUID id = (UUID) rs.getObject("id");
    String name = rs.getString("full_name");
    int age = rs.getInt("age");
    String email = rs.getString("email");
    System.out.printf("%s (%d) - %s\n", name, age, email);
}
```

### 2.3 Câu lệnh INSERT / UPDATE / DELETE

```java
String insertSql = "INSERT INTO student (full_name, age, email) VALUES (?, ?, ?)";
try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
    stmt.setString(1, "Nguyen Van A");
    stmt.setInt(2, 20);
    stmt.setString(3, "vana@gmail.com");

    int affected = stmt.executeUpdate();
    System.out.println("Rows inserted: " + affected);
}
```

---

## 3) Quản lý tài nguyên & Exception Handling

### 3.1 try-with-resources

> Dùng để tự động đóng `Connection`, `Statement`, `ResultSet`.

```java
try (Connection conn = DriverManager.getConnection(url, user, pass);
     PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
    while (rs.next()) {
        // Xử lý kết quả
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

> Khi khối `try` kết thúc, Java tự động gọi `.close()` cho tất cả resource trong ngoặc.

### 3.2 SQLException

> Là ngoại lệ đặc trưng cho lỗi JDBC, có thể xem `errorCode`, `SQLState`, `message` để debug.

```java
catch (SQLException e) {
    System.err.println("SQLState: " + e.getSQLState());
    System.err.println("ErrorCode: " + e.getErrorCode());
    System.err.println("Message: " + e.getMessage());
}
```

---

## 4) Nhược điểm của JDBC thuần

| Vấn đề                                                      | Mô tả                                                |
| ----------------------------------------------------------- | ---------------------------------------------------- |
| **Mã lặp lại nhiều**                                        | Cần nhiều code để mở, đóng, gán tham số, đọc dữ liệu |
| **Không ánh xạ Object-Relational**                          | Cần tự viết chuyển đổi giữa Object ↔ Row             |
| **Khó mở rộng & bảo trì**                                   | Khi bảng thay đổi → phải cập nhật thủ công nhiều nơi |
| **Không hỗ trợ transaction, caching, lazy loading mạnh mẽ** | → Lý do cần ORM (JPA/Hibernate)                      |

> Trong buổi tiếp theo, ta sẽ học **Spring Data JPA** để giải quyết các hạn chế này.

---

## 5) Thực hành: Kết nối PostgreSQL thật & SELECT học viên

### 5.1 Cấu trúc thư mục

```
src/main/java/student/jdbc/
 ├── JdbcSelectDemo.java
 └── JdbcInsertDemo.java
```

### 5.2 Demo: SELECT học viên

```java
public class JdbcSelectDemo {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/student_management";
        String user = "app_user";
        String pass = "123456";

        String sql = "SELECT id, full_name, age, email FROM student";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("%s | %d | %s\n",
                        rs.getString("full_name"),
                        rs.getInt("age"),
                        rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### 5.3 Bài tập thực hành

* Viết API `/api/v1/jdbc/students` đọc dữ liệu thật từ DB qua JDBC (chưa dùng JPA).
* Mở rộng thêm truy vấn có điều kiện `WHERE age >= ?`.
* So sánh độ phức tạp code JDBC với Service + Repository đã viết ở buổi 3.

---

## 6) Tóm tắt kiến thức chính

* JDBC là nền tảng kết nối DB cấp thấp trong Java.
* `DriverManager` dùng để mở `Connection`.
* `PreparedStatement` giúp tránh SQL Injection.
* `ResultSet` để đọc dữ liệu từ kết quả truy vấn.
* JDBC thuần mạnh nhưng cồng kềnh → cần ORM (JPA/Hibernate) để tự động hóa ánh xạ Object–Relational.






---
## 2) Chuẩn bị môi trường

### 2.1 Dependency (Gradle)

```groovy
dependencies {
  implementation 'org.postgresql:postgresql:42.7.4'
}
```

*Maven tương đương*

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.7.4</version>
</dependency>
```

### 2.2 Tạo bảng & seed dữ liệu mẫu (PostgreSQL)

```sql
CREATE SCHEMA IF NOT EXISTS app;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS app.students (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  full_name VARCHAR(150) NOT NULL,
  email VARCHAR(200) UNIQUE NOT NULL,
  age INT CHECK (age >= 10),
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO app.students(full_name, email, age) VALUES
 ('Nguyễn Văn A', 'a@example.com', 18),
 ('Trần Thị B',   'b@example.com', 20)
ON CONFLICT DO NOTHING;
```

### 2.3 Cấu hình kết nối (application.properties)

> Có thể tái dùng file properties của dự án Spring Boot hoặc tạo file riêng cho demo JDBC.

```properties
jdbc.url=jdbc:postgresql://localhost:5432/student_management
jdbc.user=app_user
jdbc.password=123456
jdbc.schema=app
```

---

## 3) Thực hành 1 – Kết nối PostgreSQL qua JDBC

### 3.1 Model tối thiểu

```java
// model/Student.java
package demo.jdbc.model;
import java.time.Instant;
import java.util.UUID;

public record Student(
    UUID id,
    String fullName,
    String email,
    Integer age,
    Instant createdAt
) {}
```

### 3.2 Tiện ích đọc cấu hình

```java
// util/Config.java
package demo.jdbc.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final Properties P = new Properties();
  static {
    try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
      if (in != null) P.load(in);
      else throw new IllegalStateException("Missing application.properties in classpath");
    } catch (IOException e) { throw new RuntimeException(e); }
  }
  public static String get(String key) { return P.getProperty(key); }
}
```

### 3.3 Lấy Connection (try‑with‑resources)

```java
// db/DB.java
package demo.jdbc.db;
import demo.jdbc.util.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
  public static Connection getConnection() throws SQLException {
    String url  = Config.get("jdbc.url");
    String user = Config.get("jdbc.user");
    String pass = Config.get("jdbc.password");
    return DriverManager.getConnection(url, user, pass);
  }
}
```

> **Ghi chú**: Từ JDBC 4+, không cần `Class.forName("org.postgresql.Driver")` nếu driver có trong classpath.

---

## 4) Thực hành 2 – SELECT học viên (PreparedStatement, ResultSet)

### 4.1 DAO tối thiểu với JDBC thuần

```java
// dao/StudentDao.java
package demo.jdbc.dao;
import demo.jdbc.model.Student;
import demo.jdbc.db.DB;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class StudentDao {
  public List<Student> findAll() {
    String sql = "SELECT id, full_name, email, age, created_at FROM app.students ORDER BY created_at DESC";
    try (Connection con = DB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Student> list = new ArrayList<>();
      while (rs.next()) {
        list.add(mapRow(rs));
      }
      return list;
    } catch (SQLException e) {
      throw new RuntimeException("DB error: " + e.getMessage(), e);
    }
  }

  public Optional<Student> findByEmail(String email) {
    String sql = "SELECT id, full_name, email, age, created_at FROM app.students WHERE email = ?";
    try (Connection con = DB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return Optional.of(mapRow(rs));
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("DB error: " + e.getMessage(), e);
    }
  }

  private Student mapRow(ResultSet rs) throws SQLException {
    return new Student(
      (UUID) rs.getObject("id"),
      rs.getString("full_name"),
      rs.getString("email"),
      (Integer) rs.getObject("age"),
      rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC).toInstant()
    );
  }
}
```

### 4.2 Chạy thử nhanh (Main)

```java
// App.java
package demo.jdbc;
import demo.jdbc.dao.StudentDao;

public class App {
  public static void main(String[] args) {
    StudentDao dao = new StudentDao();
    System.out.println("== All students ==");
    dao.findAll().forEach(System.out::println);

    System.out.println("== Find by email ==");
    System.out.println(dao.findByEmail("a@example.com").orElse(null));
  }
}
```

### 4.3 Vì sao dùng PreparedStatement?

* **An toàn**: tránh **SQL Injection** nhờ tham số `?` được bind.
* **Hiệu năng**: DB có thể cache plan cho câu lệnh đã chuẩn bị.
* **Đơn giản**: gán kiểu dữ liệu rõ ràng (`setString`, `setInt`, `setObject`, ...).

> **Ví dụ (KHÔNG NÊN):**

```java
String badSql = "SELECT * FROM app.students WHERE email = '" + input + "'"; // DỄ SQLi
```

---

## 5) Transaction, Batch, và Resource Management (nhanh)

### 5.1 Transaction cơ bản

```java
try (Connection con = DB.getConnection()) {
  con.setAutoCommit(false);
  try (PreparedStatement ps1 = con.prepareStatement("UPDATE ...");
       PreparedStatement ps2 = con.prepareStatement("INSERT ...")) {
    // ps1.executeUpdate(); ps2.executeUpdate();
    con.commit();
  } catch (Exception ex) {
    con.rollback();
    throw ex;
  }
}
```

### 5.2 Batch update

```java
try (Connection con = DB.getConnection();
     PreparedStatement ps = con.prepareStatement("INSERT INTO app.students(full_name,email,age) VALUES (?,?,?)")) {
  con.setAutoCommit(false);
  for (int i = 0; i < 1000; i++) {
    ps.setString(1, "Name " + i);
    ps.setString(2, "u"+i+"@example.com");
    ps.setInt(3, 18 + (i%5));
    ps.addBatch();
    if (i % 200 == 0) ps.executeBatch();
  }
  ps.executeBatch();
  con.commit();
}
```

### 5.3 Đóng tài nguyên đúng cách

* Luôn dùng **try‑with‑resources** để tự động đóng `Connection/Statement/ResultSet`.
* Không chia sẻ `Connection` giữa nhiều thread.

---

## 6) Nhược điểm của JDBC thuần

* **Nhiều boilerplate**: lặp lại khối try/catch/finally và code mapping.
* **Mapping thủ công**: dễ lỗi khi tên cột/kiểu dữ liệu đổi.
* **Khó bảo trì**: SQL rải rác, khó tái sử dụng.
* **Transaction phức tạp**: phải tự quản lý commit/rollback.

> **Hướng phát triển** (sẽ học ở buổi sau):
>
> * **Spring JdbcTemplate**: giảm boilerplate, quản lý resource/exception tốt hơn.
> * **JPA/Hibernate**: ORM, map entity ↔ table, query với JPQL/Criteria, migration Flyway.

---

## 7) Bài tập trên lớp (Hands‑on)

1. Viết hàm `findById(UUID id)` trả về `Optional<Student>`.
2. Viết hàm `searchByName(String keyword)` với `LIKE` (dùng `LOWER(full_name) LIKE LOWER(?)`).
3. In ra danh sách theo thứ tự `created_at DESC` và phân trang thủ công: thêm `LIMIT ? OFFSET ?`.

---

## 8) Quiz nhanh

1. Sự khác nhau giữa `Statement` và `PreparedStatement`?
2. `executeQuery()` và `executeUpdate()` khác nhau thế nào?
3. Vì sao cần `try‑with‑resources`?
4. Kể tên 2 nhược điểm của JDBC thuần.

---

## 9) Bài tập về nhà

1. Bổ sung các hàm `insert(...)`, `update(...)`, `deleteById(...)` cho `StudentDao` (trả về số dòng ảnh hưởng).
2. Viết method `findAll(int page, int size)` trả về list theo trang.
3. Thử cố tình truyền vào email `\"x' OR '1'='1\"` và chứng minh `PreparedStatement` ngăn chặn SQLi.
4. (Tuỳ chọn) Tạo lớp mapper tách riêng `ResultSet → Student` để dễ tái sử dụng.

---

> **Kết thúc buổi 4**: Bạn đã kết nối PostgreSQL thật bằng **JDBC**, đọc dữ liệu bằng **PreparedStatement/ResultSet** và hiểu các hạn chế của JDBC thuần – tiền đề để chuyển sang **JdbcTemplate/JPA** trong các buổi tiếp theo.

package student.management.api_app.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import student.management.api_app.constant.FieldLength;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "people", schema = "app")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "full_name", nullable = false, length = FieldLength.NAME_MAX_LENGTH)
    String fullName;

    LocalDate dob;

    @Column(unique = true, length = FieldLength.PHONE_MAX_LENGTH)
    String phone;

    @Column(name = "contact_email", length = FieldLength.EMAIL_MAX_LENGTH)
    String contactEmail;

    @Column(length = FieldLength.ADDRESS_MAX_LENGTH)
    String address;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    Instant updatedAt;

    // Chưa làm đăng nhập -> chưa dùng entity User -> tạm lược bỏ mapping user_id
}

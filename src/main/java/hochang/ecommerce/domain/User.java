package hochang.ecommerce.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

import static hochang.ecommerce.constants.NumberConstants.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(unique = true, length = INT_20)
    private String username;

    private String password;

    @Column(length = INT_10)
    private String name;

    private LocalDate birthDate;

    @Column(length = INT_40)
    private String email;

    @Column(length = INT_11)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = INT_5)
    private Role role;

    @Builder
    public User(String username, String password, String name, LocalDate birthDate, String email, String phone, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public void modifyProfile(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }
}

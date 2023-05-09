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
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{
    @Id
    @GeneratedValue() //Mysql 사용시, strategy = GenerationType.IDENTITY
    @Column(name = "user_id")
    private Long id;

    private String username; 

    private String password;

    private String name;

    private LocalDate birthDate;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String username, String password, String name, LocalDate birthDate, String email, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.role = Role.USER;
    }

    public void modifyProfile(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }
}

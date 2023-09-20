package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter @Setter
public class UserRegistration {
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{3,19}$"
            ,message = "아이디는 첫 글자는 영어로 시작하고, 그 뒤로는 영어 대/소문자, 숫자를 사용하고 3 ~ 19자리까지 입력해야 합니다. ")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+\\\\|{}\\[\\]:\";'<>?,./~`])[A-Za-z\\d!@#$%^&*()\\-_=+\\\\|{}\\[\\]:\";'<>?,./~`]{8,15}$"
            , message = "비밀번호는 대소문자, 숫자, 특수문자를 모두 포함한 8 ~ 15자리까지 입력해야 합니다.")
    private String password;

    @Size(max = 10, message = "이름은 10자 이하여야 합니다.")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,40}$", message = "이메일 형식이 옳바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11자리 숫자를 사용하고 '-'는 사용하지 않고 입력해야 합니다")
    private String phone;
}

package hochang.ecommerce.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class UserRegistrationTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("부적합한 비밀번호1 - 길이 부적합")
    public void validatePassword1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("123");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("부적합한 비밀번호2 - Blank")
    public void validatePassword2() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword(" ");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("부적합한 비밀번호3 - 특수문자 부적합")
    public void validatePassword3() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("sdfss232");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("부적합한 비밀번호4 - Blank")
    public void validatePassword4() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("sdf1234!@# ");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("적합한 비밀번호5")
    public void validatePassword5() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("tyfhvg123~!");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("적합한 아이디1")
    public void validateUsername1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUsername("tyfhvg123");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("부적합한 이메일1 - .부적합")
    public void validateEmail1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail("asdf@asdf");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("적합한 이메일2")
    public void validateEmail2() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail("asdf@asdf.cmo");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("적합한 전화번호1")
    public void validatePhone1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPhone("01022221111");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("부적합한 전화번호2 - 길이 부적합")
    public void validatePhone2() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPhone("0102222111");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }
}
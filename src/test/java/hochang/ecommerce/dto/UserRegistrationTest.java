package hochang.ecommerce.dto;

import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class UserRegistrationTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void 옳바르지않은_비밀번호1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("123");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바르지않은_비밀번호2() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword(" ");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바르지않은_비밀번호3() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바르지않은_비밀번호4() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("sdfss232");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바르지않은_비밀번호5() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("sdf1234!@# ");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바른_비밀번호1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPassword("tyfhvg123~!");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void 옳바른_아이디1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUsername("tyfhvg123");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void 옳바르지_않은_이메일1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail("asdf@asdf");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void 옳바른_이메일1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail("asdf@asdf.cmo");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void 옳바른_전화번호1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPhone("01022221111");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void 옳바르지_않은_전화번호1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setPhone("0102222111");
        //When
        Set<ConstraintViolation<UserRegistration>> violations = validator.validate(userRegistration);
        //Then
        assertThat(violations.size()).isEqualTo(1);
    }
}
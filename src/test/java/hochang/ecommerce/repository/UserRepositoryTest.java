package hochang.ecommerce.repository;

import hochang.ecommerce.config.TestConfig;
import hochang.ecommerce.domain.Role;
import hochang.ecommerce.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(TestConfig.class)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    private Long userId;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .username("asdf1234")
                .password("asdf123!@@")
                .name("asdf")
                .birthDate(LocalDate.of(1234, 12, 1))
                .email("adsf@asdf.com")
                .phone("12345678912")
                .role(Role.USER)
                .build();

        userId = userRepository.save(user).getId();
    }
    
    @Test
    @DisplayName("단일 회원 조회")
    public void findUser() {
        //Given
        //When
        User user = userRepository.findByUsername("asdf1234");
        System.out.println("user.getId() = " + user.getUsername());
        //Then
        assertThat(user.getUsername()).isEqualTo("asdf1234");
    } 
    
    @Test
    @DisplayName("모든 회원 조회")
    public void findUsers() {
        //Given
        User secondUser = User.builder()
                .username("qwer")
                .password("qwer123!@@")
                .name("qwer")
                .birthDate(LocalDate.of(2345, 12, 1))
                .email("qwer@qewr.com")
                .phone("01022223333")
                .build();
        userRepository.save(secondUser);
        //When
        List<User> all = userRepository.findAll();
        //Then
        for (User user : all) {
            System.out.println("user.getOnlineId() = " + user.getUsername());
            System.out.println("user.getId() = " + user.getId());
            System.out.println();
        }
        assertThat(all.size()).isEqualTo(2);
    } 

    @Test
    @DisplayName("회원 삭제")
    public void delete() {
        //Given
        User user = userRepository.findByUsername("asdf1234");
        System.out.println("user.getId() = " + user.getId());
        //When
        userRepository.deleteAll();
        //Then
        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("아이디로 회원 찾기")
    public void findByUsername() {
        //Given
        //When
        User user = userRepository.findByUsername("asdf1234");
        //Then
        assertThat(user.getUsername()).isEqualTo("asdf1234");
    }

    @Test
    @DisplayName("회원 정보 수정")
    void modifyUser() {
        //Given
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.modifyProfile("dlghckd1@dlghckd.com","01022223333");
        //Then
        assertThat(user.getEmail()).isEqualTo("dlghckd1@dlghckd.com");
    }
}
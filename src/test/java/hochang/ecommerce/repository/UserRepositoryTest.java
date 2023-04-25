package hochang.ecommerce.repository;

import hochang.ecommerce.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void 회원가입() {
        User user = User.builder()
                .username("asdf1234")
                .password("asdf123!@@")
                .name("asdf")
                .birthDate(LocalDate.of(1234, 12, 1))
                .email("adsf@asdf.com")
                .phone("12345678912")
                .build();
        
        Long id = userRepository.save(user);
    }
    
    @Test
    public void 단일회원조회() {
        //Given
        //When
        User user = userRepository.findByUsername("asdf1234");
        System.out.println("user.getId() = " + user.getId());
        //Then
        assertThat(user.getUsername()).isEqualTo("asdf1234");
    } 
    
    @Test
    public void 모든회원조회() {
        //Given
        User secondUser = User.builder()
                .username("qwer")
                .password("qwer123!@@")
                .name("qwer")
                .birthDate(LocalDate.of(2345, 12, 1))
                .email("qwer@qewr.com")
                .phone("01022223333")
                .build();
        Long id = userRepository.save(secondUser);
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
    public void 회원삭제() {
        //Given
        User user = userRepository.findByUsername("asdf1234");
        System.out.println("user.getId() = " + user.getId());
        //When
        userRepository.remove(user.getId());
        //Then
        assertThat(userRepository.findByUsername("asdf1234")).isNull();
    }

    @Test
    public void 아이디로_회원_찾기() {
        //Given
        //When
        User user = userRepository.findByUsername("asdf1234");
        //Then
        assertThat(user.getUsername()).isEqualTo("asdf1234");
    }
}
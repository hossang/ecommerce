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
                .onlineId("asdf1234")
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
        User user = userRepository.findByOnlineId("asdf1234");
        System.out.println("user.getId() = " + user.getId());
        //Then
        assertThat(user.getOnlineId()).isEqualTo("asdf1234");
    } 
}
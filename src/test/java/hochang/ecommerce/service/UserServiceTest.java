package hochang.ecommerce.service;

import hochang.ecommerce.domain.Role;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Test
    @DisplayName("회원가입 성공")
    public void save1() {
        //Given
        UserRegistration userRegistration = new UserRegistration();
        given(userRepository.save(any(User.class))).willReturn(any(User.class));
        //When
        User user = userService.join(userRegistration, Role.USER);
        //Then
        then(userRepository).should(times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    public void save2() {
        //Given
        UserRegistration userRegistration1 = new UserRegistration();
        UserRegistration userRegistration2 = new UserRegistration();
        given(userRepository.save(any(User.class)))
                .willReturn(any(User.class))
                .willThrow(IllegalStateException.class);
        //When
        User user = userService.join(userRegistration1, Role.USER);
        assertThatThrownBy(() -> userService.join(userRegistration2, Role.USER))
                .isInstanceOf(IllegalStateException.class);
        //Then
        then(userRepository).should(times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("모든 회원 찾기")
    public void findUsers() {
        //Given
        List<User> users = new ArrayList<>();
        users.add(User.builder().username("user1").build());
        users.add(User.builder().username("user2").build());
        given(userRepository.findAll()).willReturn(users);

        //When
        List<User> result = userService.findUsers();
        //Then
        then(userRepository).should().findAll();
        assertThat(users.size()).isEqualTo(result.size());
        assertThat(users.get(0).getUsername()).isEqualTo(result.get(0).getUsername());
        assertThat(users.get(1).getUsername()).isEqualTo(result.get(1).getUsername());
    }

}
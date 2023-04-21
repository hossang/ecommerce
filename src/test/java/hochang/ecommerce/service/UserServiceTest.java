package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.repository.UserRepository;
import hochang.ecommerce.dto.UserForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    public void 회원가입() {
        //Given
        UserForm userForm = new UserForm();
        given(userRepository.save(any(User.class))).willReturn(1L);
        //When
        Long userId = userService.join(userForm);
        //Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    public void 중복_회원가입() {
        //Given
        UserForm userForm1 = new UserForm();
        UserForm userForm2 = new UserForm();
        given(userRepository.save(any(User.class)))
                .willReturn(1L)
                .willThrow(IllegalStateException.class);
        //When
        Long userId = userService.join(userForm1);
        assertThatThrownBy(() -> userService.join(userForm2))
                .isInstanceOf(IllegalStateException.class);
        //Then
        then(userRepository).should(times(2)).save(any(User.class));
    }

}
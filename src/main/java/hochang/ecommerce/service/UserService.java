package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.repository.UserRepository;
import hochang.ecommerce.dto.UserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByOnlineId(user.getOnlineId());
        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private User toUser(UserForm userForm) {
        User user = User.builder().onlineId(userForm.getOnlineId())
                .password(userForm.getPassword())
                .name(userForm.getName())
                .birthDate(userForm.getBirthDate())
                .email(userForm.getEmail())
                .phone(userForm.getPhone())
                .build();
        return user;
    }
}

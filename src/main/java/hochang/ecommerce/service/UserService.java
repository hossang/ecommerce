package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.SignInForm;
import hochang.ecommerce.repository.UserRepository;
import hochang.ecommerce.dto.UserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public Long join(UserForm userForm) {
        User user = toUser(userForm);
        validateDuplicateUser(user);
        return userRepository.save(user);

    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public User findOne(Long userId) {
        return userRepository.findOne(userId);
    }

    public UserForm findUserFormByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return toUserForm(user);
    }

    public String signIn(SignInForm signInForm) {
        User user = userRepository.findByUsername(signInForm.getUsername());
        if (user == null || !encoder.matches(signInForm.getPassword(), user.getPassword())) {
            return null;
        }
        return user.getUsername();
    }
    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByUsername(user.getUsername());
        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private User toUser(UserForm userForm) {
        User user = User.builder().username(userForm.getUsername())
                .password(encoder.encode(userForm.getPassword()))
                .name(userForm.getName())
                .birthDate(userForm.getBirthDate())
                .email(userForm.getEmail())
                .phone(userForm.getPhone())
                .build();
        return user;
    }

    private UserForm toUserForm(User user) { 
        UserForm userForm = new UserForm();
        userForm.setUsername(user.getUsername());
        userForm.setPassword("");
        userForm.setName(user.getName());
        userForm.setBirthDate(user.getBirthDate());
        userForm.setEmail(user.getEmail());
        userForm.setPhone(user.getPhone());
        return userForm;
    }
}

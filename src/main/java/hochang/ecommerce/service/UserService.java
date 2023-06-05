package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.SignIn;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.repository.UserRepository;
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
    public Long join(UserRegistration userRegistration) {
        User user = toUser(userRegistration);
        validateDuplicateUser(user);
        return userRepository.save(user);

    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public User findOne(Long userId) {
        return userRepository.findOne(userId);
    }

    public UserRegistration findUserFormByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        return toUserForm(user);
    }

    public String signIn(SignIn signIn) {
        User user = userRepository.findUserByUsername(signIn.getUsername());
        if (user == null || !encoder.matches(signIn.getPassword(), user.getPassword())) {
            return null;
        }
        return user.getUsername();
    }

    @Transactional
    public Long modifyEmailAndPhone(UserRegistration userRegistration) {
        User user = userRepository.findUserByUsername(userRegistration.getUsername());
        log.info("user.getId() = {}", user.getId());
        if (!encoder.matches(userRegistration.getPassword(), user.getPassword())) { //리팩토링
            return null;
        }
        user.modifyProfile(userRegistration.getEmail(), userRegistration.getPhone());
        log.info("user.getId() = {}", user.getId());
        log.info("user.getEmail() = {}", user.getEmail());
        log.info("user.getPhone() = {}", user.getPhone());
        return user.getId();
    }

    @Transactional
    public Long removeUser(String username) {
        return userRepository.remove(username);
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findUserByUsername(user.getUsername());
        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private User toUser(UserRegistration userRegistration) {
        User user = User.builder().username(userRegistration.getUsername())
                .password(encoder.encode(userRegistration.getPassword()))
                .name(userRegistration.getName())
                .birthDate(userRegistration.getBirthDate())
                .email(userRegistration.getEmail())
                .phone(userRegistration.getPhone())
                .build();
        return user;
    }

    private UserRegistration toUserForm(User user) {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUsername(user.getUsername());
        userRegistration.setPassword("");
        userRegistration.setName(user.getName());
        userRegistration.setBirthDate(user.getBirthDate());
        userRegistration.setEmail(user.getEmail());
        userRegistration.setPhone(user.getPhone());
        return userRegistration;
    }
}

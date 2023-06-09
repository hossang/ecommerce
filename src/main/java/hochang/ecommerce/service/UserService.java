package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.BoardUser;
import hochang.ecommerce.dto.SignIn;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public User join(UserRegistration userRegistration) {
        User user = toUser(userRegistration);
        validateDuplicateUser(user);
        return userRepository.save(user);

    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public UserRegistration findUserRegistrationByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return toUserForm(user);
    }

    public String signIn(SignIn signIn) {
        User user = userRepository.findByUsername(signIn.getUsername());
        if (user == null || !encoder.matches(signIn.getPassword(), user.getPassword())) {
            return null;
        }
        return user.getUsername();
    }

    @Transactional
    public Long modifyEmailAndPhone(UserRegistration userRegistration) {
        User user = userRepository.findByUsername(userRegistration.getUsername());
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

    public Page<BoardUser> findBoardUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        Page<BoardUser> boardUsers = users.map(o -> toBoardUser(o));
        return boardUsers;
    }

    @Transactional
    public void removeUser(String username) {
        User user = userRepository.findByUsername(username);
        userRepository.delete(user);
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByUsername(user.getUsername());
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

    private BoardUser toBoardUser(User user) {
        BoardUser boardUser = new BoardUser();
        boardUser.setId(user.getId());
        boardUser.setUsername(user.getUsername());
        boardUser.setCreatedDate(user.getCreatedDate());
        return boardUser;
    }
}

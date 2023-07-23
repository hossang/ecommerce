package hochang.ecommerce.service;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.Role;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.BoardUser;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.repository.OrderRepository;
import hochang.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public User join(UserRegistration userRegistration, Role role) {
        User user = toUser(userRegistration, role);
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
        Optional<Order> byStatusOrder = orderRepository.findByUserAndStatus(user, OrderStatus.ORDER);
        if (byStatusOrder.isPresent()) {
            byStatusOrder.get().restoreItem();
        }
        List<Order> byUsersIdOrders = orderRepository.findByUserId(user.getId());
        if (!byUsersIdOrders.isEmpty()) {
            orderRepository.deleteAll(byUsersIdOrders);
        }
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername() : {}", username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        log.info("loadUserByUsername() user.getPassword() : {}", user.getPassword());

        return  builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByUsername(user.getUsername());
        if (findUser != null) {
            throw new IllegalStateException();
        }
    }

    //객체 매핑 메서드

    private User toUser(UserRegistration userRegistration, Role role) {
        User user = User.builder().username(userRegistration.getUsername())
                .password(encoder.encode(userRegistration.getPassword()))
                .name(userRegistration.getName())
                .birthDate(userRegistration.getBirthDate())
                .email(userRegistration.getEmail())
                .phone(userRegistration.getPhone())
                .role(role)
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

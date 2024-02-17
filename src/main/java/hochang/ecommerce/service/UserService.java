package hochang.ecommerce.service;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.Role;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.BoardUser;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.exception.UserIllegalStateException;
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
        User user = createUser(userRegistration, role);
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
        if (!encoder.matches(userRegistration.getPassword(), user.getPassword())) {
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
        return users.map(this::toBoardUser);
    }

    @Transactional
    public void removeUser(String username) {
        User user = userRepository.findByUsername(username);
        removeItemsInCart(user);

        List<Order> orders = orderRepository.findByUserId(user.getId());
        if (!orders.isEmpty()) {
            orderRepository.deleteAll(orders);
        }
        //soft delete ?
        userRepository.delete(user);
    }

    private void removeItemsInCart(User user) {
        Optional<Order> orderInCart = orderRepository.findByUserAndStatusForUpdate(user, OrderStatus.ORDER);
        if (orderInCart.isPresent()) {
            orderInCart.get().cancelItem(); //동시성 제어가 필요하다
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByUsername(user.getUsername());
        if (findUser != null) {
            throw new UserIllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private User createUser(UserRegistration userRegistration, Role role) {
        return User.builder().username(userRegistration.getUsername())
                .password(encoder.encode(userRegistration.getPassword()))
                .name(userRegistration.getName())
                .birthDate(userRegistration.getBirthDate())
                .email(userRegistration.getEmail())
                .phone(userRegistration.getPhone())
                .role(role)
                .build();
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

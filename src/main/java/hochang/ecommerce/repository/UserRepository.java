package hochang.ecommerce.repository;

import hochang.ecommerce.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager entityManager;

    public Long save(User user) {
        entityManager.persist(user);
        return user.getId();
    }

    public User findOne(Long id) {
        return entityManager.find(User.class, id);
    }

    public List<User> findAll() {
        return entityManager.createQuery("select u from User u", User.class)
                .getResultList();
    }

    public User findUserByUsername(String username) {
        return entityManager.createQuery("select u from User u where u.username= :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Long remove(String username) {
        User userByUsername = findUserByUsername(username);
        entityManager.remove(userByUsername);
        log.info("userByUsername : {}", userByUsername.getId());
        return userByUsername.getId();
    }

}

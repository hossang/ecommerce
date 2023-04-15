package hochang.ecommerce.repository;

import hochang.ecommerce.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager entityManager;

    public Long save(User user) {
        entityManager.persist(user);
        return user.getId();
    }
}

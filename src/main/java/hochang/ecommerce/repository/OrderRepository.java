package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id =:id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    Page<Order> findByStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);

    List<Order> findByUserId(Long userId);
}

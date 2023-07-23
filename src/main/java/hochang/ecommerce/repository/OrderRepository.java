package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);

    Page<Order> findByStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);

    List<Order> findByUserId(Long userId);
}

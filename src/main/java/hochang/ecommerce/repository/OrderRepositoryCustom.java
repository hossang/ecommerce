package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> findOrdersWithCoveringIndex(Long userId, List<OrderStatus> orderStatuses, Pageable pageable);
}

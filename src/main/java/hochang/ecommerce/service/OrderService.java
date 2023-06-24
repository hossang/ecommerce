package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderLine;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.BoardOrder;
import hochang.ecommerce.dto.OrderItem;
import hochang.ecommerce.dto.OrderStatusConstants;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.repository.OrderRepository;
import hochang.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //
    @Transactional
    public Order createOrder(String username, Long itemId, int quantity) {
        User user = userRepository.findByUsername(username);
        OrderLine orderLine = createOrderLine(itemId, quantity);
        Order order = Order.builder()
                .user(user)
                .orderLine(orderLine)
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public Order addOrderLine(Order order, Long itemId, int quantity) {
        for (OrderLine orderLine : order.getOrderLines()) {
            if (orderLine.getItem().getId().equals(itemId)) {
                orderLine.modifyCount(quantity);
                order.calculateTotalPrice();
                return order;
            }
        }
        OrderLine orderLine = createOrderLine(itemId, quantity);
        order.addOrderLine(orderLine);
        order.calculateTotalPrice();
        return order;
    }

    @Transactional
    public void completeOrder(Order order) {
        order.completeOrder();
    }

    @Transactional
    public void cancelOrder(Order order) {
        order.cancelOrder();
    }

    public Optional<Order> findByUserAndStatus(String username) {
        User user = userRepository.findByUsername(username);
        return orderRepository.findByUserAndStatus(user, OrderStatus.ORDER);
    }

    //

    private OrderLine createOrderLine(Long itemId, int quantity) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = optionalItem.get();
        OrderLine orderLine = OrderLine.builder()
                .item(item)
                .count(quantity)
                .build();
        return orderLine;
    }

    //

}

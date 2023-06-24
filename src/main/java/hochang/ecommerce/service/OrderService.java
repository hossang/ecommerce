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

    public List<OrderItem> findOrderItems(List<OrderLine> orderLines) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            orderItems.add(toOrderItem(orderLine));
        }
        return orderItems;
    }


    public BoardOrder findBoardOrder(Long id) {
        Order order = findOrder(id);
        BoardOrder boardOrder = toBoardOrder(order);
        return boardOrder;
    }

    public Order findOrder(Long id) {
        return orderRepository.findById(id).get();
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

    private OrderItem toOrderItem(OrderLine orderLine) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(orderLine.getItem().getId());
        orderItem.setName(orderLine.getItem().getName());
        orderItem.setPrice(orderLine.getItem().getPrice());
        orderItem.setCount(orderLine.getCount());
        orderItem.setOrderPrice(orderLine.getOrderPrice());
        orderItem.setStoreFileName(orderLine.getItem().getStoreFileName());
        return orderItem;
    }

    private BoardOrder toBoardOrder(Order o) {
        BoardOrder boardOrder = new BoardOrder();
        StringBuffer stringBuffer = new StringBuffer();
        boardOrder.setId(o.getId());

        for (OrderLine orderLine : o.getOrderLines()) {
            stringBuffer.append(orderLine.getItem().getName()).append(", ");
        }
        stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length() - 1);
        boardOrder.setOrderLineNames(stringBuffer.toString());

        boardOrder.setOrderStatue(OrderStatusConstants.ORDER_STATUS_MAP.get(o.getStatus()));
        boardOrder.setTotalPrice(o.getTotalPrice());
        boardOrder.setCreateDate(o.getCreatedDate());
        return boardOrder;
    }
}

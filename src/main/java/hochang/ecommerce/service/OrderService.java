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

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    public static final int COMMA = 2;
    public static final int BLANK = 1;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void order(String username, Long itemId, Integer quantity) {
        Optional<Order> optionalOrder = findByUserAndStatus(username);
        if (!optionalOrder.isPresent()) {
            createOrder(username, itemId, quantity);
            return;
        }
        addOrderLineInOrder(optionalOrder.orElseThrow(EntityNotFoundException::new), itemId, quantity);
    }

    @Transactional
    public void completeOrder(Order order) {
        order.completeOrder();
    }

    @Transactional
    public void cancelOrder(Long id) {
        //동시성 재어 필요
        Order order = orderRepository.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
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

    public Page<BoardOrder> findBoardOrders(Pageable pageable, String username) {
        User user = userRepository.findByUsername(username);
        Page<Order> orderPage = orderRepository.findByStatusInAndUserId(List.of(OrderStatus.COMPLETE, OrderStatus.CANCEL),
                user.getId(), pageable);
        return orderPage.map(this::toBoardOrder);
    }

    public BoardOrder findBoardOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return toBoardOrder(order);
    }

    public List<OrderLine> findOrderLines(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return order.getOrderLines();
    }

    private Order createOrder(String username, Long itemId, int quantity) {
        User user = userRepository.findByUsername(username);
        OrderLine orderLine = createOrderLine(itemId, quantity);
        Order order = Order.builder()
                .user(user)
                .orderLine(orderLine)
                .build();

        return orderRepository.save(order);
    }

    private Order addOrderLineInOrder(Order order, Long itemId, int quantity) {
        for (OrderLine orderLine : order.getOrderLines()) {
            if (isItemInOrderLine(itemId, orderLine)) {
                orderLine.modifyCount(quantity); //동시성 제어가 필요하다
                order.calculateTotalPrice();
                return order;
            }
        }

        OrderLine orderLine = createOrderLine(itemId, quantity);
        order.addOrderLine(orderLine);
        order.calculateTotalPrice();
        return order;
    }

    private OrderLine createOrderLine(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        OrderLine orderLine = OrderLine.builder()
                .item(item)
                .count(quantity) //동시성 제어가 필요하다
                .build();
        return orderLine;
    }

    private boolean isItemInOrderLine(Long itemId, OrderLine orderLine) {
        return orderLine.getItem().getId().equals(itemId);
    }

    private String makeOrderLineNames(Order o) {
        StringBuilder stringbuilder = new StringBuilder();
        for (OrderLine orderLine : o.getOrderLines()) {
            stringbuilder.append(orderLine.getItem().getName()).append(", ");
        }
        stringbuilder.delete(stringbuilder.length() - COMMA, stringbuilder.length() - BLANK);
        return stringbuilder.toString();
    }

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
        boardOrder.setId(o.getId());
        boardOrder.setOrderLineNames(makeOrderLineNames(o));
        boardOrder.setOrderStatue(OrderStatusConstants.ORDER_STATUS_MAP.get(o.getStatus()));
        boardOrder.setTotalPrice(o.getTotalPrice());
        boardOrder.setCreateDate(o.getCreatedDate());
        return boardOrder;
    }
}

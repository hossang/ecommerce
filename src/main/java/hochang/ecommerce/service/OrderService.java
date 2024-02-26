package hochang.ecommerce.service;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderLine;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.ShippingAddress;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.BoardOrder;
import hochang.ecommerce.dto.OrderingUser;
import hochang.ecommerce.dto.OrderItem;
import hochang.ecommerce.repository.AccountRepository;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.repository.OrderRepository;
import hochang.ecommerce.repository.ShippingAddressRepository;
import hochang.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
    private static final int COMMA = 2;
    private static final int BLANK = 1;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order addOrderLineInCart(User user, Long itemId, Integer quantity) {
        Optional<Order> optionalOrder = orderRepository.findByUserAndStatusForUpdate(user, OrderStatus.ORDER);
        if (!optionalOrder.isPresent()) {
            return createOrder(user, itemId, quantity);

        }
        return addOrderLine(optionalOrder.orElseThrow(EntityNotFoundException::new), itemId, quantity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrder(User user, OrderItem orderItem, ShippingAddress shippingAddress, Account account) {
        OrderLine orderLine = createOrderLine(orderItem.getItemId(), orderItem.getQuantity());
        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .account(account)
                .orderLine(orderLine)
                .build();
        Long orderId = orderRepository.save(order).getId();
        completeOrder(orderId);

        return order;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void completeOrder(User user, ShippingAddress shippingAddress, Account account) {
        Order order = orderRepository.findByUserAndStatusForUpdate(user, OrderStatus.ORDER)
                .orElseThrow(EntityNotFoundException::new);
        order.linkForeignEntity(shippingAddress, account);
        order.completeOrder();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Order findOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<Order> findOrderByUserAndStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    public OrderingUser findOrderingUser(Long id) {
        Order order = findOrder(id);
        return toOrderingUser(order);
    }

    public List<OrderItem> findOrderItems(List<OrderLine> orderLines) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            orderItems.add(toOrderItem(orderLine));
        }
        return orderItems;
    }

    public OrderItem findOrderItem(Item item, Integer quantity) {
        return toOrderItem(item,quantity);
    }

    public Page<BoardOrder> findBoardOrders(Pageable pageable, User user, List<OrderStatus> orderStatuses) {
        Page<Order> orderPage = orderRepository.findOrdersWithCoveringIndex(user.getId()
                , orderStatuses, pageable);
        return orderPage.map(this::toBoardOrder);
    }

    public BoardOrder findBoardOrder(Long id) {
        Order order = findOrder(id);
        return toBoardOrder(order);
    }

    public List<OrderLine> findOrderLines(Long id) {
        Order order = findOrder(id);
        return order.getOrderLines();
    }

    public void completeOrder(Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(EntityNotFoundException::new);
        order.completeOrder();
    }

    private boolean isItemInOrderLine(Long itemId, OrderLine orderLine) {
        return orderLine.getItem().getId().equals(itemId);
    }

    private String makeOrderLineNames(Order order) {
        StringBuilder stringbuilder = new StringBuilder();
        for (OrderLine orderLine : order.getOrderLines()) {
            stringbuilder.append(orderLine.getItem().getName()).append(", "); //N + 1
        }
        if (!stringbuilder.isEmpty()) {
            stringbuilder.delete(stringbuilder.length() - COMMA, stringbuilder.length() - BLANK);
        }
        return stringbuilder.toString();
    }

    private Order createOrder(User user, Long itemId, int quantity) {
        OrderLine orderLine = createOrderLine(itemId, quantity);
        Order order = Order.builder()
                .user(user)
                .orderLine(orderLine)
                .build();

        return orderRepository.save(order);
    }

    private Order addOrderLine(Order order, Long itemId, int quantity) {
        for (OrderLine orderLine : order.getOrderLines()) {
            if (isItemInOrderLine(itemId, orderLine)) {
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

    private OrderLine createOrderLine(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        OrderLine orderLine = OrderLine.builder()
                .item(item)
                .quantity(quantity)
                .build();
        return orderLine;
    }

    private OrderItem toOrderItem(OrderLine orderLine) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(orderLine.getItem().getId());
        orderItem.setName(orderLine.getItem().getName()); //N + 1
        orderItem.setPrice(orderLine.getItem().getPrice());
        orderItem.setQuantity(orderLine.getQuantity());
        orderItem.setOrderPrice(orderLine.getOrderPrice());
        orderItem.setStoreFileName(orderLine.getItem().getThumbnailStoreFileName());
        return orderItem;
    }


    private OrderItem toOrderItem(Item item, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setName(item.getName());
        orderItem.setPrice(item.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setOrderPrice(item.getPrice() * quantity);
        orderItem.setStoreFileName(item.getThumbnailStoreFileName());
        return orderItem;
    }


    private BoardOrder toBoardOrder(Order order) {
        BoardOrder boardOrder = new BoardOrder();
        boardOrder.setId(order.getId());
        boardOrder.setOrderLineNames(makeOrderLineNames(order));
        boardOrder.setStatus(OrderStatusConstants.ORDER_STATUSES.get(order.getStatus()));
        boardOrder.setTotalPrice(order.getTotalPrice());
        boardOrder.setCreateDate(order.getCreatedDate());
        return boardOrder;
    }

    private OrderingUser toOrderingUser(Order order) {
        OrderingUser orderingUser = new OrderingUser();
        ShippingAddress shippingAddress = order.getShippingAddress();
        Account account = order.getAccount();
        orderingUser.setShippingAddressId(shippingAddress.getId());
        orderingUser.setFullAddress(shippingAddress.getAddress() + " " + shippingAddress.getDetailAddress());
        orderingUser.setFullAccount(account.getBank() + " " + account.getAccountNumber());
        return orderingUser;
    }
}

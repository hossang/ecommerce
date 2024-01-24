package hochang.ecommerce.web;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderLine;
import hochang.ecommerce.domain.ShippingAddress;
import hochang.ecommerce.dto.OrderingUser;
import hochang.ecommerce.dto.BoardOrder;
import hochang.ecommerce.dto.OrderAddress;
import hochang.ecommerce.dto.OrderItem;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.service.OrderService;
import hochang.ecommerce.service.ShippingAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static hochang.ecommerce.web.PageConstants.END_RANGE;
import static hochang.ecommerce.web.PageConstants.PREVENTION_NEGATIVE_NUMBERS;
import static hochang.ecommerce.web.PageConstants.PREVENTION_ZERO;
import static hochang.ecommerce.web.PageConstants.START_RANGE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ItemService itemService;
    private final ShippingAddressService shippingAddressService;


    @GetMapping("/users/{username}/orders/cart")
    public String orderLineList(@PathVariable String username, @RequestParam(required = false) Long itemId,
                                @RequestParam(required = false) Integer quantity, Model model) {
        if (isInsufficientPurchase(itemId, quantity)) {
            return "error/400";
        }
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        if (isEmptyCart(itemId, quantity, optionalOrder)) {
            return "users/myEmptyCart";
        }
        Order order = optionalOrder.orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("username", username);
        model.addAttribute("id", order.getId());
        model.addAttribute("orderItems", orderItems);
        return "users/myCart";
    }

    @PostMapping("/users/{username}/orders/cart")
    public String orderLineCreate(@PathVariable String username, @RequestParam(required = false) Long itemId,
                                  @RequestParam(required = false) Integer quantity) {
        if (isItemsToAddInCart(itemId, quantity)) {
            orderService.addOrderLineInCart(username, itemId, quantity);
        }

        return "redirect:/users/{username}/orders/cart";
    }

    @GetMapping("/users/{username}/orders/{id}/create")
    public String orderDetails(@PathVariable String username, @PathVariable Long id, Model model) {
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        if (!optionalOrder.isPresent()) {
            return "users/myEmptyCart";
        }
        Order order = optionalOrder.orElseThrow(EntityNotFoundException::new);
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(username);
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("totalPrice", order.getTotalPrice());
        model.addAttribute("availableOrderAddresses", availableOrderAddresses);
        model.addAttribute("orderItems", orderItems);
        return "users/myOrders";
    }

    @PostMapping("/users/{username}/orders/{id}/create")
    public String orderCreate(@PathVariable String username, @PathVariable Long id, OrderingUser orderingUser) {
        Order order = orderService.findByUserAndStatus(username).orElseThrow(EntityNotFoundException::new);
        ShippingAddress shippingAddress = shippingAddressService
                .findShippingAddress(orderingUser.getShippingAddressId());
        order.linkForeignEntity(shippingAddress);
        orderService.completeOrder(order);
        return "redirect:/users/{username}/orders";
    }

    @GetMapping("/users/{username}/orders/create")
    public String orderDetails(@PathVariable String username, @RequestParam(required = false) Long itemId,
                               @RequestParam(required = false) Integer quantity, Model model) {
        if (!isItemsToAddInCart(itemId, quantity)) {
            return "error/400";
        }
        Optional<Item> optionalItem = itemService.findById(itemId);
        Item item = optionalItem.orElseThrow(EntityNotFoundException::new);
        if (item.getCount() < quantity) {
            throw new IllegalArgumentException(item.createExceptionMessage());
        }
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(username);
        OrderItem orderItem = orderService.findOrderItem(item, quantity);
        model.addAttribute("availableOrderAddresses", availableOrderAddresses);
        model.addAttribute("totalPrice", orderItem.getOrderPrice());
        model.addAttribute("orderItem", orderItem);
        return "users/myOrder";
    }

    @PostMapping("/users/{username}/orders/create")
    public String orderCreate(@PathVariable String username, OrderItem orderItem, OrderingUser orderingUser) {
        Order order = orderService.createOrder(username, orderItem, orderingUser);
        orderService.completeOrder(order);
        return "redirect:/users/{username}/orders";
    }


    @GetMapping("/users/{username}/orders")
    public String orderList(@PathVariable String username, @PageableDefault Pageable pageable, Model model) {
        Page<BoardOrder> boardOrders = orderService.findBoardOrders(pageable, username);
        int nowPage = boardOrders.getPageable().getPageNumber() + PREVENTION_ZERO;
        int startPage = Math.max(PREVENTION_NEGATIVE_NUMBERS, nowPage - START_RANGE);
        int endPage = Math.min(boardOrders.getTotalPages(), nowPage + END_RANGE);

        model.addAttribute("username", username);
        model.addAttribute("boardOrders", boardOrders);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "users/orderList";
    }

    @GetMapping("/users/{username}/orders/{id}")
    public String orderDetail(@PathVariable String username, @PathVariable Long id, Model model) {
        BoardOrder boardOrder = orderService.findBoardOrder(id);
        List<OrderLine> orderLines = orderService.findOrderLines(id);
        List<OrderItem> orderItems = orderService.findOrderItems(orderLines);
        OrderingUser orderingUser = orderService.findOrderingUser(boardOrder.getId());
        model.addAttribute("boardOrder", boardOrder);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("orderingUser", orderingUser);
        return "users/orderDetail";
    }

    @PostMapping("/users/{username}/orders/{id}/cancel")
    public String orderCancel(@PathVariable String username, @PathVariable Long id) {
        orderService.cancelOrder(id);
        return "redirect:/users/{username}/orders";
    }

    private boolean isEmptyCart(Long itemId, Integer quantity, Optional<Order> optionalOrder) {
        return !optionalOrder.isPresent() && itemId == null && quantity == null;
    }

    private boolean isInsufficientPurchase(Long itemId, Integer quantity) {
        return (itemId != null && quantity == null) || (itemId == null && quantity != null);
    }

    private boolean isItemsToAddInCart(Long itemId, Integer quantity) {
        return itemId != null && quantity != null;
    }
}

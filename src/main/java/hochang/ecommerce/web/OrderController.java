package hochang.ecommerce.web;

import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderLine;
import hochang.ecommerce.dto.BoardOrder;
import hochang.ecommerce.dto.OrderItem;
import hochang.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/users/{username}/orders/cart")
    public String orderLineList(@PathVariable String username, @RequestParam(required = false) Long itemId
            , @RequestParam(required = false) Integer quantity, Model model) {
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        if (!optionalOrder.isPresent() && itemId == null && quantity == null) {
            return "users/myEmptyCart";
        }
        Order order = getOrder(username, itemId, quantity, optionalOrder);
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("orderItems", orderItems);
        return "users/myCart";
    }

    @PostMapping("/users/{username}/orders/cart")
    public String orderLineCreate(@PathVariable String username, @RequestParam(required = false) Long itemId
            , @RequestParam(required = false) Integer quantity) {
        Long id = orderService.findByUserAndStatus(username).get().getId();
        return "redirect:/users/{username}/orders/" + id +"/create";
    }

    @GetMapping("/users/{username}/orders/{id}/create")
    public String  orderDetails(@PathVariable String username, @PathVariable Long id, Model model) {
        Order order = orderService.findByUserAndStatus(username).get();
        model.addAttribute("totalPrice", order.getTotalPrice());
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("orderItems", orderItems);
        return "users/myOrder";
    }

    @PostMapping("/users/{username}/orders/{id}/create")
    public String orderCreate(@PathVariable String username, @PathVariable Long id) {
        Order order = orderService.findByUserAndStatus(username).get();
        orderService.completeOrder(order);
        return "redirect:/users/{username}/orders";
    }

    private Order getOrder(String username, Long itemId, Integer quantity, Optional<Order> optionalOrder) {
        if (!optionalOrder.isPresent()) {
            return orderService.createOrder(username, itemId, quantity);
        }
        if (itemId == null && quantity == null) {
            return optionalOrder.get();
        }
        return orderService.addOrderLine(optionalOrder.get(), itemId, quantity);
    }
}

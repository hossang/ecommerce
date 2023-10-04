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

    @GetMapping("/users/{username}/orders/cart")
    public String orderLineList(@PathVariable String username, @RequestParam(required = false) Long itemId,
                                @RequestParam(required = false) Integer quantity, Model model) {
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        if (!optionalOrder.isPresent() && itemId == null && quantity == null) {
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
        if (itemId != null && quantity != null) {
            orderService.order(username, itemId, quantity);
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
        model.addAttribute("totalPrice", order.getTotalPrice());
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("orderItems", orderItems);
        return "users/myOrder";
    }

    @PostMapping("/users/{username}/orders/{id}/create")
    public String orderCreate(@PathVariable String username, @PathVariable Long id) {
        Order order = orderService.findByUserAndStatus(username).orElseThrow(EntityNotFoundException::new);
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
        model.addAttribute("boardOrder", boardOrder);
        model.addAttribute("orderItems", orderItems);
        return "users/orderDetail";
    }

    @PostMapping("/users/{username}/orders/{id}/cancel")
    public String orderCancel(@PathVariable String username, @PathVariable Long id) {
        orderService.cancelOrder(id);
        return "redirect:/users/{username}/orders";
    }
}

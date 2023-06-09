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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
        Order order = optionalOrder.get();
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("username", username);
        model.addAttribute("id", order.getId());
        model.addAttribute("orderItems", orderItems);
        return "users/myCart";
    }

    @PostMapping("/users/{username}/orders/cart")
    public String orderLineCreate(@PathVariable String username, @RequestParam(required = false) Long itemId
            , @RequestParam(required = false) Integer quantity) {
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        Order order = getOrder(username, itemId, quantity, optionalOrder);

        return "redirect:/users/{username}/orders/cart";
    }

    @GetMapping("/users/{username}/orders/{id}/create")
    public String  orderDetails(@PathVariable String username, @PathVariable Long id, Model model) {
        Optional<Order> optionalOrder = orderService.findByUserAndStatus(username);
        if (!optionalOrder.isPresent()) {
            return "users/myEmptyCart";
        }
        Order order = optionalOrder.get();
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

    @GetMapping("/users/{username}/orders")
    public String orderList(@PathVariable String username
            , @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<BoardOrder> boardOrders = orderService.findBoardOrders(pageable);
        int nowPage = boardOrders.getPageable().getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 4);
        int endPage = Math.min(boardOrders.getTotalPages(),nowPage + 5);

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
        List<OrderLine> orderLines = orderService.findOrder(id).getOrderLines();
        List<OrderItem> orderItems = orderService.findOrderItems(orderLines);
        model.addAttribute("boardOrder", boardOrder);
        model.addAttribute("orderItems", orderItems);
        return "users/orderDetail";
    }

    @PostMapping("/users/{username}/orders/{id}/cancel")
    public String orderCancel(@PathVariable String username, @PathVariable Long id) {
        Order order = orderService.findOrder(id);
        orderService.cancelOrder(order);
        return "redirect:/users/{username}/orders";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String outOfStock(IllegalArgumentException illegalArgumentException, HttpServletRequest request
            , RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        log.info("referer: {}", referer);
        if (referer == null) {
            referer = "/";
        }
        redirectAttributes.addAttribute("errorMessage", illegalArgumentException.getMessage());
        return "redirect:" + referer;
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

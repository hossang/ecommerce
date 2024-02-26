package hochang.ecommerce.web;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderLine;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.ShippingAddress;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.OrderAccount;
import hochang.ecommerce.dto.OrderingUser;
import hochang.ecommerce.dto.BoardOrder;
import hochang.ecommerce.dto.OrderAddress;
import hochang.ecommerce.dto.OrderItem;
import hochang.ecommerce.exception.ItemIllegalArgumentException;
import hochang.ecommerce.service.AccountService;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.service.OrderService;
import hochang.ecommerce.service.ShippingAddressService;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
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
    private final AccountService accountService;
    private final UserService userService;

    @GetMapping("/users/{username}/orders/cart")
    public String orderLineList(@PathVariable String username, @RequestParam(required = false) Long itemId,
                                @RequestParam(required = false) Integer quantity, Model model) {
        if (isInsufficientPurchase(itemId, quantity)) {
            return "error/400";
        }
        User user = userService.findByUsername(username);
        Optional<Order> optionalOrder = orderService.findOrderByUserAndStatus(user, OrderStatus.ORDER);
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
            User user = userService.findByUsername(username);
            orderService.addOrderLineInCart(user, itemId, quantity);
        }

        return "redirect:/users/{username}/orders/cart";
    }

    @GetMapping("/users/{username}/orders/{id}/create")
    public String orderDetails(@PathVariable String username, @PathVariable Long id,
                               @RequestParam(required = false) String errorMessage, Model model) {
        User user = userService.findByUsername(username);
        Optional<Order> optionalOrder = orderService.findOrderByUserAndStatus(user, OrderStatus.ORDER);
        if (!optionalOrder.isPresent()) {
            return "users/myEmptyCart";
        }
        Order order = optionalOrder.orElseThrow(EntityNotFoundException::new);
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(user);
        List<OrderAccount> availableOrderAccounts = accountService.findOrderAccounts(user);
        List<OrderItem> orderItems = orderService.findOrderItems(order.getOrderLines());
        model.addAttribute("totalPrice", order.getTotalPrice());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("availableOrderAddresses", availableOrderAddresses);
        model.addAttribute("availableOrderAccounts", availableOrderAccounts);
        model.addAttribute("orderItems", orderItems);
        return "users/myOrders";
    }

    @PostMapping("/users/{username}/orders/{id}/create")
    public String orderCreate(@PathVariable String username, @PathVariable Long id, OrderingUser orderingUser) {
        User user = userService.findByUsername(username);
        ShippingAddress shippingAddress = shippingAddressService
                .findShippingAddress(orderingUser.getShippingAddressId());
        Account account = accountService.findAccount(orderingUser.getAccountId());
        orderService.completeOrder(user, shippingAddress, account);
        return "redirect:/users/{username}/orders";
    }

    @GetMapping("/users/{username}/orders/create")
    public String orderDetails(@PathVariable String username, @RequestParam(required = false) Long itemId,
                               @RequestParam(required = false) Integer quantity,
                               @RequestParam(required = false) String errorMessage, Model model) {
        if (!isItemsToAddInCart(itemId, quantity)) {
            return "error/400";
        }
        Item item = itemService.findById(itemId);
        User user = userService.findByUsername(username);
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(user);
        List<OrderAccount> availableOrderAccounts = accountService.findOrderAccounts(user);
        OrderItem orderItem = orderService.findOrderItem(item, quantity);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("availableOrderAddresses", availableOrderAddresses);
        model.addAttribute("availableOrderAccounts", availableOrderAccounts);
        model.addAttribute("totalPrice", orderItem.getOrderPrice());
        model.addAttribute("orderItem", orderItem);
        return "users/myOrder";
    }

    @PostMapping("/users/{username}/orders/create")
    public String orderCreate(@PathVariable String username, OrderItem orderItem, OrderingUser orderingUser) {
        User user = userService.findByUsername(username);
        ShippingAddress shippingAddress = shippingAddressService
                .findShippingAddress(orderingUser.getShippingAddressId());
        Account account = accountService.findAccount(orderingUser.getAccountId());
        orderService.createOrder(user, orderItem, shippingAddress, account);
        /*동시성 테스트 해보기*/
        return "redirect:/users/{username}/orders";
    }


    @GetMapping("/users/{username}/orders")
    public String orderList(@PathVariable String username, @PageableDefault Pageable pageable, Model model) {
        User user = userService.findByUsername(username);
        Page<BoardOrder> boardOrders = orderService.findBoardOrders(pageable, user,
                List.of(OrderStatus.COMPLETE, OrderStatus.CANCEL));
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

package hochang.ecommerce.web;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.OrderAddress;
import hochang.ecommerce.service.ShippingAddressService;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;
    private final UserService userService;

    @PostMapping("/users/{username}/shippingAddresses/register")
    @ResponseBody
    public List<OrderAddress> shippingAddressCreate(@PathVariable String username,
                                                    @RequestBody @Valid OrderAddress orderAddress,
                                                    BindingResult bindingResult) {
        User user = userService.findByUsername(username);
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(user);
        if (bindingResult.hasErrors()) {
            return availableOrderAddresses;
        }
        OrderAddress savedOrderAddress = shippingAddressService.save(user, orderAddress);
        availableOrderAddresses.add(savedOrderAddress);
        return availableOrderAddresses;
    }
}

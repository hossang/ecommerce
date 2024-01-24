package hochang.ecommerce.web;

import hochang.ecommerce.dto.OrderAddress;
import hochang.ecommerce.service.ShippingAddressService;
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

    @PostMapping("/users/{username}/shippingAddresses/register")
    @ResponseBody
    public List<OrderAddress> shippingAddressCreate(@PathVariable String username,
                                                    @RequestBody @Valid OrderAddress orderAddress,
                                                    BindingResult bindingResult) {
        List<OrderAddress> availableOrderAddresses = shippingAddressService.findOrderAddresses(username);
        if (bindingResult.hasErrors()) {
            return availableOrderAddresses;
        }
        OrderAddress savedOrderAddress = shippingAddressService.save(username, orderAddress);
        availableOrderAddresses.add(savedOrderAddress);
        return availableOrderAddresses;
    }
}

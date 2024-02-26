package hochang.ecommerce.web;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.OrderAccount;
import hochang.ecommerce.service.AccountService;
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
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    @PostMapping("/users/{username}/accounts/register")
    @ResponseBody
    public List<OrderAccount> accountCreate(@PathVariable String username,
                                            @RequestBody @Valid OrderAccount orderAccount,
                                            BindingResult bindingResult) {
        User user = userService.findByUsername(username);
        List<OrderAccount> availableOrderAccounts = accountService.findOrderAccounts(user);
        if (bindingResult.hasErrors()) {
            return availableOrderAccounts;
        }
        OrderAccount savedOrderAccount = accountService.save(orderAccount, user);
        availableOrderAccounts.add(savedOrderAccount);
        return availableOrderAccounts;
    }
}

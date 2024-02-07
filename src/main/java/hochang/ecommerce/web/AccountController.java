package hochang.ecommerce.web;

import hochang.ecommerce.dto.OrderAccount;
import hochang.ecommerce.service.AccountService;
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

    @PostMapping("/users/{username}/accounts/register")
    @ResponseBody
    public List<OrderAccount> accountCreate(@PathVariable String username,
                                            @RequestBody @Valid OrderAccount orderAccount,
                                            BindingResult bindingResult) {
        List<OrderAccount> availableOrderAccounts = accountService.findOrderAccounts(username);
        if (bindingResult.hasErrors()) {
            return availableOrderAccounts;
        }
        OrderAccount savedOrderAccount = accountService.save(username,orderAccount);
        availableOrderAccounts.add(savedOrderAccount);
        return availableOrderAccounts;
    }
}

package hochang.ecommerce.web;

import hochang.ecommerce.web.argumentresolver.SignIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/")
    public String showHomePage(@SignIn String signInUser, Model model) {
        log.info("signInUser = {}",signInUser);
        if (signInUser == null) {
            return "guests/home";
        }

        model.addAttribute("signInUser", signInUser);
        return "users/signInHome";
    }
}

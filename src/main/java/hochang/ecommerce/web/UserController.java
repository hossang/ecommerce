package hochang.ecommerce.web;

import hochang.ecommerce.dto.UserForm;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/signup";
    }

    @PostMapping("/signup")
    public String create(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }
        userService.join(userForm);
        return "redirect:/";
    }
}

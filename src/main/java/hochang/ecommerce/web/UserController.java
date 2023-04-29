package hochang.ecommerce.web;

import hochang.ecommerce.dto.SignInForm;
import hochang.ecommerce.dto.UserForm;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/sign-up")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "guests/signUp";
    }

    @PostMapping("/sign-up")
    public String create(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "guests/signUp";
        }
        userService.join(userForm);
        return "redirect:/";
    }

    @GetMapping("/sign-in")
    public String createSignInForm(SignInForm signInForm) {
        return "guests/signIn";
    }

    @PostMapping("/sign-in")
    public String signIn(@Valid SignInForm signInForm, BindingResult bindingResult
            , HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {
        if (bindingResult.hasErrors()) {
            return "guests/signIn";
        }
        String username = userService.signIn(signInForm);
        log.info("username : {}", username);
        if (username == null) {
            bindingResult.reject("signInFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "guests/signIn";
        }
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.SIGN_IN_USER, username);
        return "redirect:" + redirectURL;
    }

    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String SignUpDuplicateUser(IllegalStateException illegalStateException, Model model) {
        String errorMessage = illegalStateException.getMessage();
        log.info("errorMessage : {}", errorMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userForm", new UserForm());
        return "guests/signUp";
    }
}

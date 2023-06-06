package hochang.ecommerce.web;

import hochang.ecommerce.dto.SignIn;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/sign-up")
    public String userRegistrationFormCreate(Model model, @RequestParam(required = false) String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userRegistration", new UserRegistration());
        return "guests/signUp";
    }

    @PostMapping("/sign-up")
    public String userRegistrationCreate(@Valid UserRegistration userRegistration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "guests/signUp";
        }
        userService.join(userRegistration);
        return "redirect:/";
    }

    @GetMapping("/sign-in")
    public String signInFormCreate(SignIn signIn) {
        return "guests/signIn";
    }

    @PostMapping("/sign-in")
    public String signInCreate(@Valid SignIn signIn, BindingResult bindingResult
            , HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {
        if (bindingResult.hasErrors()) {
            return "guests/signIn";
        }
        String username = userService.signIn(signIn);
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
    public String signOutCreate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/users/{username}/modify")
    public String userRegistrationFormModify(@PathVariable String username, Model model) {
        UserRegistration userRegistration = userService.findUserRegistrationByUsername(username);
        model.addAttribute("userRegistration", userRegistration);
        return "users/userProfileModification";
    }

    @PostMapping("/users/{username}/modify")
    public String userRegistrationModify(@Valid UserRegistration userRegistration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/userProfileModification";
        }
        if (userService.modifyEmailAndPhone(userRegistration) == null) {
            bindingResult.reject("improperPassword", "비밀번호가 맞지 않습니다.");
            return "users/userProfileModification";
        }
        return "redirect:/users/{username}";
    }

    @GetMapping("/users/{username}")
    public String myPage(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        return "users/myPage";
    }

    @GetMapping("/users/{username}/remove")
    public String userFormRemove(@PathVariable String username, Model model) {
        model.addAttribute("uesrname", username);
        return "users/userRemoval";
    }

    @PostMapping("/users/{username}/remove")
    public String userRemove(@PathVariable String username, HttpSession session) { //Q HtttpServletRequest하고 HttpSession 하는 거랑 무슨 차이?
        userService.removeUser(username);
        session.invalidate();
        return "redirect:/";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String SignUpDuplicateUser(IllegalStateException illegalStateException, RedirectAttributes redirectAttributes) {
        String errorMessage = illegalStateException.getMessage();
        log.info("errorMessage : {}", errorMessage);
        redirectAttributes.addAttribute("errorMessage", errorMessage);
        return "redirect:/sign-up";
    }
    //회원 목록 url 추가
}

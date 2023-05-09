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
    public String createForm(Model model, @RequestParam(required = false) String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
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

    @GetMapping("/users/{username}/modify")
    public String createProfileModification(@PathVariable String username, Model model) {
        UserForm userForm = userService.findUserFormByUsername(username);
        model.addAttribute("userForm", userForm);
        return "users/userProfileModification";
    }

    @PostMapping("/users/{username}/modify")
    public String modifyProfile(@Valid UserForm userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/userProfileModification";
        }
        if (userService.modifyEmailAndPhone(userForm) == null) {
            bindingResult.reject("improperPassword", "비밀번호가 맞지 않습니다.");
            return "users/userProfileModification";
        }

        return "redirect:/users/{username}";
    }

    @GetMapping("/users/{username}")
    public String createMyPage(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        return "users/MyPage";
    }

    @GetMapping("/users/{username}/withdraw")
    public String withdrawUser(@PathVariable String username, Model model) {
        model.addAttribute("uesrname", username);
        return "users/userWithdrawl";
    }

    @PostMapping("/users/{username}/withdraw")
    public String withdrawUser(@PathVariable String username, HttpSession session) { //Q HtttpServletRequest하고 HttpSession 하는 거랑 무슨 차이?
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
}

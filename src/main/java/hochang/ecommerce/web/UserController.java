package hochang.ecommerce.web;

import hochang.ecommerce.domain.Role;
import hochang.ecommerce.dto.BoardUser;
import hochang.ecommerce.dto.SignIn;
import hochang.ecommerce.dto.UserRegistration;
import hochang.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static hochang.ecommerce.web.PageConstants.END_RANGE;
import static hochang.ecommerce.web.PageConstants.PREVENTION_NEGATIVE_NUMBERS;
import static hochang.ecommerce.web.PageConstants.PREVENTION_ZERO;
import static hochang.ecommerce.web.PageConstants.START_RANGE;

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
        userService.join(userRegistration, Role.USER);
        return "redirect:/";
    }

    @GetMapping("/sign-in")
    public String signInFormCreate(SignIn signIn, Model model, @RequestParam(required = false) String error) {
        if (error != null && error.equals("true")) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호를 잘못 입력했습니다.");
        }
        return "guests/signIn";
    }

    @GetMapping("/users/{username}/modify")
    public String userRegistrationFormModify(@PathVariable String username, Model model) {
        UserRegistration userRegistration = userService.findUserRegistrationByUsername(username);
        model.addAttribute("userRegistration", userRegistration);
        return "users/userProfileModification";
    }

    @PostMapping("/users/{username}/modify")
    public String userRegistrationModify(@PathVariable String username
            , @Valid UserRegistration userRegistration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/userProfileModification";
        }
        if (userService.modifyEmailAndPhone(userRegistration) == null) {
            bindingResult.reject("improperPassword", "비밀번호가 맞지 않습니다.");
            return "users/userProfileModification";
        }
        return "redirect:/";
    }

    @GetMapping("/users/{username}/remove")
    public String userFormRemove(@PathVariable String username, Model model) {
        model.addAttribute("uesrname", username);
        return "users/userRemoval";
    }

    @PostMapping("/users/{username}/remove")
    public String userRemove(@PathVariable String username, HttpSession session) {
        userService.removeUser(username);
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/admins/{username}/users")
    public String userList(@PathVariable String username
            , @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable, Model model) {
        Page<BoardUser> boardUsers = userService.findBoardUsers(pageable);
        int nowPage = boardUsers.getPageable().getPageNumber() + PREVENTION_ZERO;
        int startPage = Math.max(PREVENTION_NEGATIVE_NUMBERS, nowPage - START_RANGE);
        int endPage = Math.min(boardUsers.getTotalPages(), nowPage + END_RANGE);

        model.addAttribute("username", username);
        model.addAttribute("boardUsers", boardUsers);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "admins/userList";
    }
}

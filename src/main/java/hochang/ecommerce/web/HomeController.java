package hochang.ecommerce.web;

import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.web.annotation.SignIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ItemService itemService;
    @GetMapping("/")
    public String homePage(@SignIn String username,
                           @PageableDefault(sort = "views", direction = Sort.Direction.DESC, size = 8) Pageable pageable,
                           Model model) {
        Page<MainItem> mainItems = itemService.findMainItem(pageable);
        int nowPage = mainItems.getPageable().getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 4);
        int endPage = Math.min(mainItems.getTotalPages(),nowPage + 5);

        model.addAttribute("username", username);
        model.addAttribute("mainItems", mainItems);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "guests/home";
    }
}

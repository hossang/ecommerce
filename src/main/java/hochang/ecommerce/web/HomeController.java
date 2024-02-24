package hochang.ecommerce.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.web.annotation.SignIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static hochang.ecommerce.web.PageConstants.PAGE_SIZE_MAIN_ITEM;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {


    private final ItemService itemService;

    @GetMapping("/")
    public String homePage(@SignIn String username,
                           @PageableDefault(size = PAGE_SIZE_MAIN_ITEM) Pageable pageable, Model model) {
        Page<MainItem> mainItems = itemService.findRealtimePopularMainItems(pageable);

        model.addAttribute("username", username);
        model.addAttribute("mainItems", mainItems);
        return "guests/home";
    }
}

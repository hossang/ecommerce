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

import static hochang.ecommerce.web.PageConstants.END_RANGE;
import static hochang.ecommerce.web.PageConstants.PAGE_SIZE_MAIN_ITEM;
import static hochang.ecommerce.web.PageConstants.PREVENTION_NEGATIVE_NUMBERS;
import static hochang.ecommerce.web.PageConstants.PREVENTION_ZERO;
import static hochang.ecommerce.web.PageConstants.START_RANGE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {


    private final ItemService itemService;

    @GetMapping("/")
    public String homePage(@SignIn String username,
                           @PageableDefault(sort = "views", direction = Sort.Direction.DESC, size = PAGE_SIZE_MAIN_ITEM) Pageable pageable,
                           Model model) {
        Page<MainItem> mainItems = itemService.findMainItemsWithCoveringIndex(pageable);
        int nowPage = mainItems.getPageable().getPageNumber() + PREVENTION_ZERO;
        int startPage = Math.max(PREVENTION_NEGATIVE_NUMBERS, nowPage - START_RANGE);
        int endPage = Math.min(mainItems.getTotalPages(), nowPage + END_RANGE);

        model.addAttribute("username", username);
        model.addAttribute("mainItems", mainItems);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "guests/home";
    }
}

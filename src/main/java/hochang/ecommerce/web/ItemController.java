package hochang.ecommerce.web;

import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.web.annotation.SignIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;

import static hochang.ecommerce.web.PageConstants.END_RANGE;
import static hochang.ecommerce.web.PageConstants.PREVENTION_NEGATIVE_NUMBERS;
import static hochang.ecommerce.web.PageConstants.PREVENTION_ZERO;
import static hochang.ecommerce.web.PageConstants.START_RANGE;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/admins/{username}/items")
    public String itemList(@PathVariable String username,
                           @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                           Model model) {
        Page<BoardItem> boardItems = itemService.findBoardItems(pageable);
        int nowPage = boardItems.getPageable().getPageNumber() + PREVENTION_ZERO;
        int startPage = Math.max(PREVENTION_NEGATIVE_NUMBERS, nowPage - START_RANGE);
        int endPage = Math.min(boardItems.getTotalPages(), nowPage + END_RANGE);

        log.info("nowPage : {}", nowPage);
        model.addAttribute("username", username);
        model.addAttribute("boardItems", boardItems);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admins/itemList";
    }

    @GetMapping("/admins/{username}/items/register")
    public String itemRegistrationFormCreate(@PathVariable String username, ItemRegistration itemRegistration,
                                             Model model) {
        model.addAttribute("username", username);
        model.addAttribute("itemRegistration", itemRegistration);
        return "admins/itemRegistration";
    }

    @PostMapping("/admins/{username}/items/register")
    public String itemRegistrationCreate(@PathVariable String username, @Valid ItemRegistration itemRegistration,
                                         BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return "admins/itemRegistration";
        }
        itemService.save(itemRegistration);
        return "redirect:/admins/{username}/items";
    }

    @GetMapping("/items/{id}")
    public String bulletinItemDetails(@SignIn String username
            , @PathVariable Long id, Model model, @RequestParam(required = false) String errorMessage) {
        BulletinItem bulletinItem = itemService.findBulletinItem(id);
        model.addAttribute("username", username);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("bulletinItem", bulletinItem);
        return "guests/itemPurchase";
    }

    @GetMapping("/admins/{username}/items/{id}/modify")
    public String itemRegistrationFormModify(@PathVariable String username, @PathVariable Long id, Model model) {
        ItemRegistration itemRegistration = itemService.findItemRegistration(id);
        model.addAttribute("username", username);
        model.addAttribute("itemRegistration", itemRegistration);
        return "admins/itemModification";
    }

    @PostMapping("/admins/{username}/items/{id}/modify")
    public String itemRegistrationModify(@PathVariable String username, @PathVariable Long id,
                                         @Valid ItemRegistration itemRegistration,
                                         BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return "admins/itemModification";
        }
        itemService.modifyItem(itemRegistration);
        return "redirect:/admins/{username}/items";

    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return itemService.getImage(filename);
    }
}

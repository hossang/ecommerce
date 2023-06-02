package hochang.ecommerce.web;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistrationForm;
import hochang.ecommerce.service.ItemService;
import hochang.ecommerce.util.file.FileStore;
import hochang.ecommerce.util.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final FileStore fileStore;

    @GetMapping("/admins/items")
    public String createItemList(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<BoardItem> boardItems = itemService.createBoardItems(pageable);
        int nowPage = boardItems.getPageable().getPageNumber() + 1;
        int startPage = Math.max(1, nowPage - 4);
        int endPage = Math.min(boardItems.getTotalPages(),nowPage + 5);

        model.addAttribute("boardItems", boardItems);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admins/itemlist";
    }

    @GetMapping("/admins/items/register")
    public String createItemRegistrationForm(ItemRegistrationForm itemRegistrationForm) {
        return "admins/itemRegistration";
    }

    @PostMapping("/admins/items/register")
    public String registerItem(ItemRegistrationForm itemRegistrationForm) throws IOException {
        UploadFile uploadFile = fileStore.storeFile(itemRegistrationForm.getImageFile());
        Long itemId = itemService.save(itemRegistrationForm, uploadFile);
        return "redirect:/admins/items";
    }

    @GetMapping("/items/{itemId}")
    public String createBulletinItem(@PathVariable Long itemId, Model model) {
        BulletinItem bulletinItem = itemService.findBulletinItem(itemId);
        model.addAttribute("bulletinItem", bulletinItem);
        return "guests/itemPurchase";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }
}

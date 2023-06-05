package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistrationForm;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.util.file.FileStore;
import hochang.ecommerce.util.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @Transactional
    public Long save(ItemRegistrationForm itemRegistrationForm, UploadFile uploadFile) throws IOException {
        Item item = toItem(itemRegistrationForm, uploadFile);
        return itemRepository.save(item).getId();
    }

    public Page<BoardItem> createBoardItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        Page<BoardItem> boardItems = itemPage.map(o -> toBoardItem(o));
        return boardItems;
    }

    public BulletinItem findBulletinItem(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = optionalItem.get();
        return toBulletinItem(item);
    }

    public ItemRegistrationForm findItemRegistrationForm(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = optionalItem.get();
        return toItemRegistration(item);
    }

    @Transactional
    public void modifyItemForm(ItemRegistrationForm itemRegistrationForm, UploadFile uploadFile) throws IOException {
        Optional<Item> optionalItem = itemRepository.findById(itemRegistrationForm.getId());
        Item item = optionalItem.get();
        log.info("item.getStoreFileName() = {}", item.getStoreFileName());
        log.info("uploadFile.getStoreFileName() = {}", uploadFile.getStoreFileName());
        fileStore.deleteFile(item.getStoreFileName());
        item.modifyItemForm(itemRegistrationForm.getName(), itemRegistrationForm.getCount(), itemRegistrationForm.getPrice(),
                itemRegistrationForm.getContents(), uploadFile.getUploadFileName(), uploadFile.getStoreFileName());
    }
    //toxxx()들 어댑터로 만들어 볼까?

    private ItemRegistrationForm toItemRegistration(Item item) {
        ItemRegistrationForm itemRegistrationForm = new ItemRegistrationForm();
        itemRegistrationForm.setId(item.getId());
        itemRegistrationForm.setName(item.getName());
        itemRegistrationForm.setCount(item.getCount());
        itemRegistrationForm.setPrice(item.getPrice());
        itemRegistrationForm.setContents(item.getContents());
        return itemRegistrationForm;
    }

    private Item toItem(ItemRegistrationForm itemRegistrationForm, UploadFile uploadFile) {
        return Item.builder()
                .name(itemRegistrationForm.getName())
                .count(itemRegistrationForm.getCount())
                .price(itemRegistrationForm.getPrice())
                .contents(itemRegistrationForm.getContents())
                .uploadFileName(uploadFile.getUploadFileName())
                .storeFileName(uploadFile.getStoreFileName())
                .build();
    }

    private BoardItem toBoardItem(Item item) {
        BoardItem boardItem = new BoardItem();
        boardItem.setId(item.getId());
        boardItem.setName(item.getName());
        boardItem.setCreatedDate(item.getCreatedDate());
        return boardItem;
    }

    private BulletinItem toBulletinItem(Item item) {
        BulletinItem bulletinItem = new BulletinItem();
        bulletinItem.setName(item.getName());
        bulletinItem.setPrice(item.getPrice());
        bulletinItem.setContents(item.getContents());
        bulletinItem.setStoreFileName(item.getStoreFileName());
        return bulletinItem;
    }
}

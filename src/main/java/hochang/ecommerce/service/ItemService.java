package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.util.file.FileStore;
import hochang.ecommerce.util.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @Transactional
    public Long save(ItemRegistration itemRegistration) throws IOException {
        UploadFile uploadFile = fileStore.storeFile(itemRegistration.getImageFile());
        Item item = toItem(itemRegistration, uploadFile);
        return itemRepository.save(item).getId();
    }

    public Page<BoardItem> findBoardItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        Page<BoardItem> boardItems = itemPage.map(o -> toBoardItem(o));
        return boardItems;
    }

    public Page<MainItem> findMainItem(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        Page<MainItem> mainItems = itemPage.map(o -> toMainItem(o));
        return mainItems;
    }

    @Transactional
    public BulletinItem findBulletinItem(Long itemId) {
        Item item = itemRepository.findByIdForUpdate(itemId).get(); //동시성 제어가 필요하다
        item.addViews();
        return toBulletinItem(item);
    }

    public ItemRegistration findItemRegistration(Long itemId) {
        Item item = itemRepository.findById(itemId).get();
        return toItemRegistration(item);
    }

    @Transactional
    public void modifyItem(ItemRegistration itemRegistration) throws IOException {
        String originalFilename = itemRegistration.getImageFile().getOriginalFilename();
        Item item = itemRepository.findById(itemRegistration.getId()).get();
        if (originalFilename.equals(item.getUploadFileName())) {
            item.modifyItem(itemRegistration);
            return;
        }
        UploadFile uploadFile = fileStore.storeFile(itemRegistration.getImageFile());
        fileStore.deleteFile(item.getStoreFileName());
        item.modifyItem(itemRegistration, uploadFile);
    }

    //
    public Resource getImage(String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }
    
    //객체 매핑 메서드
    private ItemRegistration toItemRegistration(Item item) {
        ItemRegistration itemRegistration = new ItemRegistration();
        itemRegistration.setId(item.getId());
        itemRegistration.setName(item.getName());
        itemRegistration.setCount(item.getCount());
        itemRegistration.setPrice(item.getPrice());
        itemRegistration.setContents(item.getContents());
        return itemRegistration;
    }

    private Item toItem(ItemRegistration itemRegistration, UploadFile uploadFile) {
        return Item.builder()
                .name(itemRegistration.getName())
                .count(itemRegistration.getCount())
                .price(itemRegistration.getPrice())
                .contents(itemRegistration.getContents())
                .uploadFileName(uploadFile.getUploadFileName())
                .storeFileName(uploadFile.getStoreFileName())
                .build();
    }

    private BoardItem toBoardItem(Item item) {
        BoardItem boardItem = new BoardItem();
        boardItem.setId(item.getId());
        boardItem.setName(item.getName());
        boardItem.setCreatedDate(item.getCreatedDate());
        boardItem.setViews(item.getViews());
        return boardItem;
    }

    private BulletinItem toBulletinItem(Item item) {
        BulletinItem bulletinItem = new BulletinItem();
        bulletinItem.setId(item.getId());
        bulletinItem.setName(item.getName());
        bulletinItem.setPrice(item.getPrice());
        bulletinItem.setContents(item.getContents());
        bulletinItem.setStoreFileName(item.getStoreFileName());
        return bulletinItem;
    }

    private MainItem toMainItem(Item item) {
        MainItem mainItem = new MainItem();
        mainItem.setId(item.getId());
        mainItem.setName(item.getName());
        mainItem.setPrice(item.getPrice());
        mainItem.setStoreFileName(item.getStoreFileName());
        return mainItem;
    }
}

package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistration;
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

    private Item findItem(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Item item = optionalItem.get();
        return item;
    }

    public BulletinItem findBulletinItem(Long itemId) {
        Item item = findItem(itemId);
        return toBulletinItem(item);
    }

    public ItemRegistration findItemRegistration(Long itemId) {
        Item item = findItem(itemId);
        return toItemRegistration(item);
    }

    @Transactional
    public void modifyItem(ItemRegistration itemRegistration) throws IOException {
        UploadFile uploadFile = fileStore.storeFile(itemRegistration.getImageFile());
        Item item = findItem(itemRegistration.getId());
        log.info("item.getStoreFileName() = {}", item.getStoreFileName());
        log.info("uploadFile.getStoreFileName() = {}", uploadFile.getStoreFileName());
        fileStore.deleteFile(item.getStoreFileName());
        item.modifyItem(itemRegistration.getName(), itemRegistration.getCount(), itemRegistration.getPrice(),
                itemRegistration.getContents(), uploadFile.getUploadFileName(), uploadFile.getStoreFileName());
    }

    @Transactional
    public void removeItem(Long id) throws IOException {
        Item item = findItem(id);
        fileStore.deleteFile(item.getStoreFileName());
        itemRepository.delete(item);
    }

    //
    public Resource getImage(String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }
    
    //
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
}

package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.util.file.S3FileStore;
import hochang.ecommerce.util.file.UploadFile;
import hochang.ecommerce.util.serialization.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static hochang.ecommerce.constants.NumberConstants.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private static final ConcurrentMap<Long, Long> VIEWS_INCREMENTS = new ConcurrentHashMap<>();
    private static final int HALF_AN_HOUR = 1_800_000;
    private static final String CLOUDFRONT_DOMAIN = "https://d14cet1pxkvpbm.cloudfront.net/";
    private final ItemRepository itemRepository;
    private final JSONUtil jsonUtil;
    private final S3FileStore fileStore;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public Long save(ItemRegistration itemRegistration) throws IOException {
        UploadFile uploadFile = fileStore.storeFile(itemRegistration.getImageFile());
        Item item = toItem(itemRegistration, uploadFile);
        return itemRepository.save(item).getId();
    }

    public Page<BoardItem> findBoardItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return itemPage.map(this::toBoardItem);
    }

    public Page<MainItem> findMainItems(Pageable pageable) {
        return itemRepository.findMainItemsWithCoveringIndex(pageable);
    }

    public BulletinItem findBulletinItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new); //동시성 제어가 필요하다
        VIEWS_INCREMENTS.put(itemId, VIEWS_INCREMENTS.getOrDefault(itemId, LONG_0) + LONG_1);
        return toBulletinItem(item);
    }

    public ItemRegistration findItemRegistration(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        return toItemRegistration(item);
    }

    @Transactional
    public void modifyItem(ItemRegistration itemRegistration) throws IOException {
        String originalFilename = itemRegistration.getImageFile().getOriginalFilename();

        Item item = itemRepository.findById(itemRegistration.getId()).orElseThrow(EntityNotFoundException::new);
        if (isImageNameSamePreviousImageName(originalFilename, item)) {
            item.modifyItem(itemRegistration);
            return;
        }

        UploadFile uploadFile = fileStore.storeFile(itemRegistration.getImageFile());
        fileStore.deleteS3File(item.getStoreFileName());
        item.modifyItem(itemRegistration, uploadFile);
    }

    public Resource getImage(String filename) throws MalformedURLException {
        String url = CLOUDFRONT_DOMAIN + fileStore.getS3FullPath(filename);
        return new UrlResource(url);
    }

    @Transactional
    @Scheduled(fixedRate = HALF_AN_HOUR)
    public void modifyViews() {
        for (Long id : VIEWS_INCREMENTS.keySet()) {
            itemRepository.incrementViewsById(id, VIEWS_INCREMENTS.get(id));
        }
        VIEWS_INCREMENTS.clear();
    }

    private static boolean isImageNameSamePreviousImageName(String originalFilename, Item item) {
        return originalFilename != null && originalFilename.equals(item.getUploadFileName());
    }

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
}

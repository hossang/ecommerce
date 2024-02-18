package hochang.ecommerce.service;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Content;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.dto.ItemSearch;
import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.dto.UploadedItemFile;
import hochang.ecommerce.repository.AccountRepository;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.repository.UserRepository;
import hochang.ecommerce.util.file.S3FileStore;
import hochang.ecommerce.util.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static hochang.ecommerce.constants.CacheConstants.*;
import static hochang.ecommerce.constants.NumberConstants.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private static final int HALF_AN_HOUR = 1_800_000;
    private static final String CLOUDFRONT_DOMAIN = "https://d14cet1pxkvpbm.cloudfront.net/";
    private ConcurrentMap<Long, Long> viewCounter = new ConcurrentHashMap<>(INT_100); //Long,Long이 아니라 BulletinItem, Long은 어떤가...
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final S3FileStore fileStore;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public Long save(ItemRegistration itemRegistration, String username) throws IOException {
        UploadFile thumbnailUploadFile = fileStore.storeFile(itemRegistration.getThumbnailImage());
        List<UploadFile> contentUploadFiles = convertMultipartFileToContentUploadFiles(itemRegistration);

        Account account = accountRepository.findById(itemRegistration.getAccountId())
                .orElseThrow(EntityNotFoundException::new);
        Item item = createItem(account, itemRegistration, thumbnailUploadFile);

        createContents(contentUploadFiles, item);
        return itemRepository.save(item).getId();
    }

    public Page<BoardItem> findBoardItems(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return itemPage.map(this::toBoardItem);
    }

    //@Cacheable(cacheNames = FIND_MAIN_ITEMS_WITH_COVERING_INDEX, key = "#pageable.pageSize.toString().concat('-').concat(#pageable.pageNumber)")
    public Page<MainItem> findMainItems(Pageable pageable) {
        /* cacheRedisTemplate 을 고려해봤는데 이것도 구린내가 남
        * 1. redis에 id만 저장하고, id의 순서를 찾을 수 있다. But! id를 통해 DB에서 Item들을 찾아와야 한다.
        * */
        return itemRepository.findMainItemsWithCoveringIndex(pageable);
    }

    public Page<MainItem> findSearchedMainItems(Pageable pageable, ItemSearch itemSearch) {
        return itemRepository.findSearchedMainItems(pageable, itemSearch);
    }
    
    @Cacheable(cacheNames = FIND_BULLETIN_ITEM, key = "#itemId")
    public BulletinItem findBulletinItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        return toBulletinItem(item);
    }

    public void increaseViews(Long itemId) {
        viewCounter.put(itemId, viewCounter.getOrDefault(itemId, LONG_0) + LONG_1);
    }

    public ItemRegistration findItemRegistration(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        return toItemRegistration(item);
    }

    @Transactional
    @CacheEvict(cacheNames = FIND_BULLETIN_ITEM, key = "#itemRegistration.id")
    public void modifyItem(ItemRegistration itemRegistration) throws IOException {
        UploadFile thumbnailUploadFile = fileStore.storeFile(itemRegistration.getThumbnailImage());
        List<UploadFile> contentUploadFiles = convertMultipartFileToContentUploadFiles(itemRegistration);

        Account account = accountRepository.findById(itemRegistration.getAccountId())
                .orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(itemRegistration.getId()).orElseThrow(EntityNotFoundException::new);

        //일단 다 삭제후 새로저장
        fileStore.deleteS3File(item.getThumbnailStoreFileName());
        itemRepository.modifyItem(item.getId(), itemRegistration.getQuantity(), thumbnailUploadFile.getUploadFileName(),
                thumbnailUploadFile.getStoreFileName(),account);
        List<Content> contents = item.getContents();
        contents.clear();

        createContents(contentUploadFiles, item);
    }

    public UploadedItemFile findUploadedItemFile(Long id) {
        Item item = findById(id);
        return toUploadedItemFile(item);
    }

    public Resource getImage(String filename) throws MalformedURLException {
        String url = CLOUDFRONT_DOMAIN + fileStore.getS3FullPath(filename);
        return new UrlResource(url);
    }

    @Transactional
    @Scheduled(fixedRate = HALF_AN_HOUR)
    public void modifyViews() {
        for (Long id : viewCounter.keySet()) {
            itemRepository.incrementViewsById(id, viewCounter.get(id));
            cacheRedisTemplate.opsForHash().increment("item_views", id, viewCounter.get(id));
        }
        viewCounter = new ConcurrentHashMap<>(INT_100);
    }


    private List<UploadFile> convertMultipartFileToContentUploadFiles(ItemRegistration itemRegistration) throws IOException {
        List<UploadFile> contentUploadFiles = new ArrayList<>();
        for (MultipartFile contentImage : itemRegistration.getContentImages()) {
            if (!StringUtils.equals(contentImage.getOriginalFilename(), "")) {
                contentUploadFiles.add(fileStore.storeFile(contentImage));
            }
        }
        return contentUploadFiles;
    }

    private void createContents(List<UploadFile> contentUploadFiles, Item item) {
        int imageQuantity = contentUploadFiles.size();
        for (int index = 0; index < imageQuantity; index++) {
            Content content = createContent(contentUploadFiles.get(index));
            item.addContent(content);
        }
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
    }

    private Item createItem(Account account,ItemRegistration itemRegistration, UploadFile uploadFile) {
        return Item.builder()
                .account(account)
                .name(itemRegistration.getName())
                .quantity(itemRegistration.getQuantity())
                .price(itemRegistration.getPrice())
                .thumbnailUploadFileName(uploadFile.getUploadFileName())
                .thumbnailStoreFileName(uploadFile.getStoreFileName())
                .build();
    }

    private Content createContent(UploadFile uploadFile) {
        return Content.builder()
                .imageStoreFileName(uploadFile.getStoreFileName())
                .imageUploadFileName(uploadFile.getUploadFileName())
                .build();

    }

    private ItemRegistration toItemRegistration(Item item) {
        ItemRegistration itemRegistration = new ItemRegistration();
        itemRegistration.setId(item.getId());
        itemRegistration.setName(item.getName());
        itemRegistration.setQuantity(item.getQuantity());
        itemRegistration.setPrice(item.getPrice());
        itemRegistration.setAccountId(item.getAccount().getId());
        return itemRegistration;
    }

    private BoardItem toBoardItem(Item item) {
        BoardItem boardItem = new BoardItem();
        boardItem.setId(item.getId());
        boardItem.setName(item.getName());
        boardItem.setCreatedDate(item.getCreatedDate());
        boardItem.setViews(item.getViews() + viewCounter.getOrDefault(item.getId(), LONG_0));
        return boardItem;
    }

    private BulletinItem toBulletinItem(Item item) {
        BulletinItem bulletinItem = new BulletinItem();
        bulletinItem.setId(item.getId());
        bulletinItem.setName(item.getName());
        bulletinItem.setPrice(item.getPrice());
        bulletinItem.setThumbnailStoreFileName(item.getThumbnailStoreFileName());
        for (Content content : item.getContents()) {
            bulletinItem.getImageStoreFileNames().add(content.getImageStoreFileName());
        }
        return bulletinItem;
    }

    private UploadedItemFile toUploadedItemFile(Item item) {
        UploadedItemFile uploadedItemFile = new UploadedItemFile();
        uploadedItemFile.setThumbnailUploadFileName(item.getThumbnailUploadFileName());
        List<String> imageUploadFileNames = uploadedItemFile.getImageUploadFileNames();
        for (Content content : item.getContents()) {
            imageUploadFileNames.add(content.getImageUploadFileName());
        }
        return uploadedItemFile;
    }
}

package hochang.ecommerce.service;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.Item;
import hochang.ecommerce.domain.Role;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.repository.AccountRepository;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {
    public static final int VIEWS = 10_000;

    @Autowired
    private ItemService itemService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    EntityManager entityManager;

    private Long itemId;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .username("asdf12345")
                .password("asdf123!@@")
                .name("asdf")
                .birthDate(LocalDate.of(1234, 12, 1))
                .email("adsf@asdf.com")
                .phone("12345678912")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Account account = Account.builder()
                .user(user)
                .bank("우리")
                .accountNumber("1234567890")
                .balance(100_000_000)
                .accountHolder("이호창")
                .build();
        accountRepository.save(account);

        Item item = Item.builder()
                .account(account)
                .name("상품1")
                .quantity(1_000)
                .price(1_000)
                .thumbnailStoreFileName("xxx")
                .thumbnailUploadFileName("xxx")
                .build();

        itemId = itemRepository.save(item).getId();
    }

    @Test
    @DisplayName("아이템 조회")
    void findOne() {
        //Given
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //Then
        assertThat(item.getName()).isEqualTo("상품1");
    }

/*    @Test
    @DisplayName("아이템 변경")
    void modifyItem() {
        //Given
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //When
        itemRepository.modifyItem(item.getId(), item.getQuantity(), "ooo",
                "ooo", item.getAccount());
        entityManager.clear();
        item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //Then
        assertThat(item.getThumbnailUploadFileName()).isEqualTo("ooo");
    }*/

    @Test
    @DisplayName("조회수 증가시키기")
    void increaseViews1() {
        //Given
        //When
        for (int i = 0; i < 10; i++) {
            itemRepository.incrementViewsById(itemId, 1L);
        }
        entityManager.clear();
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //Then
        assertThat(item.getViews()).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 증가시키기 - 동시성테스트 성공하지만 @Transactional임에도 롤백 실패")
    public void increaseViews2() throws InterruptedException {
        //Given
        int threadCount = VIEWS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long before = 0L;
        //When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    itemRepository.incrementViewsById(itemId, 1L);
                    System.out.println("안녕하세요");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        entityManager.clear();
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        long after = item.getViews();
        long result = after - before;
        //Then
        System.out.println("result = " + result);
        assertThat(result).isEqualTo(VIEWS);
    }

    @Test
    @DisplayName("아이템 수량 증가시키기 - 동시성테스트")
    void increaseQuantity() throws InterruptedException {
        //Given
        int threadCount = VIEWS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long before = 0L;
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //When
        for (int i = 0; i < threadCount; i++) {
            Item finalItem = item;
            executorService.submit(() -> {
                try {
                    finalItem.addQuantity(1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        long after = item.getQuantity();
        long result = after - before;
        //Then
        System.out.println("result = " + result);
        assertThat(result).isNotEqualTo(VIEWS);
    }

    @Test
    @DisplayName("아이템 수량 감소시키기 - 동시성테스트")
    void decreaseQuantity() throws InterruptedException {
        //Given
        int threadCount = VIEWS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long before = 20_000L;
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        item.addQuantity(20_000);
        //When
        for (int i = 0; i < threadCount; i++) {
            Item finalItem = item;
            executorService.submit(() -> {
                try {
                    finalItem.reduceQuantity(1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        long after = item.getQuantity();
        long result = before - after;
        //Then
        System.out.println("result = " + result);
        assertThat(result).isNotEqualTo(VIEWS);
    }

    @Test
    @DisplayName("ConcurrentHashMap.clear()시 사이즈 테스트")
    void clearConcurrentHashMap() {
        //Given
        Map<Integer, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        //When
        map.clear();
        //Then
        System.out.println("map.size() = " + map.size());

    }
}
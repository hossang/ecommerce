package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {
    public static final int VIEWS = 100;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void 아이템_생성() {
        Item item = Item.builder()
                .name("상품1")
                .count(10_000)
                .price(10_000)
                .contents(".")
                .storeFileName("test1")
                .uploadFileName("test1")
                .build();
        itemRepository.save(item);
    }

    @Test
    public void db에서_엔티티_가져오기() {
        //Given
        List<Item> all = itemRepository.findAll();
        Item item = all.get(0);
        System.out.println("item.getId() = " + item.getId());
        //When

        //Then
        System.out.println("item.getViews() = " + item.getViews());
        assertThat(item.getViews()).isEqualTo(0);

    }

    @Test
    public void 조회수동시성테스트_성공_멀티스레드_롤백실패() throws InterruptedException {
        //Given
        int threadCount = VIEWS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Item> all = itemRepository.findAll();

        Item item = all.get(0);
        Long id = item.getId();
        System.out.println("item.getId() = " + item.getId());

        long bef = item.getViews();

        //When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    itemService.findBulletinItem(id);
                } finally {
                    // 스레드 작업 완료시 latch 값을 감소시킴
                    latch.countDown();
                }
            });
        }
        latch.await(); //다른 쓰레드에서 수행중인 작업이 완료될때까지 기다려줌
        //Then
        item = itemRepository.findById(id).get(); //값이 바뀌었는데 DB에서 안 읽어옴
        long aft = item.getViews();
        assertThat(aft - bef).isNotEqualTo(VIEWS);
    }

    @Test
    public void 조회수동시성테스트_성공_단일스레드_롤백성공() throws InterruptedException {
        //Given
        List<Item> all = itemRepository.findAll();
        Item item = all.get(0);
        Long id = item.getId();
        System.out.println("item.getId() = " + item.getId());

        long bef = item.getViews();

        //When
        itemService.findBulletinItem(id);
        itemService.findBulletinItem(id);
        //Then
        item = itemRepository.findById(id).get();
        long aft = item.getViews();
        assertThat(aft - bef).isEqualTo(2L);
    }

    @Test
    public void 조회수동시성테스트_실패() throws InterruptedException {
        //뭐지 롤백이 안돼
        //Given
        int threadCount = VIEWS;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Item> all = itemRepository.findAll();
        Item item = all.get(0);
        System.out.println("item.getId() = " + item.getId());

        long before = item.getViews();

        //When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    itemService.findBulletinItem(item.getId());
                } finally {
                    // 스레드 작업 완료시 latch 값을 감소시킴
                    latch.countDown();
                }
            });
        }
        latch.await();
        //Then
        long after = item.getViews();
        System.out.println("(after-before) = " + (after - before));
    }
}
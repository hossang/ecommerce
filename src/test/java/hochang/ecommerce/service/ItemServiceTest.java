package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void db에서_엔티티_가져오기() {
        //Given
        Item item = itemRepository.findById(2L).get();
        //When

        //Then
        System.out.println("item.getViews() = " + item.getViews());
        assertThat(item.getViews()).isEqualTo(2L);

    }

    @Test
    public void 조회수동시성테스트_성공_멀티스레드_롤백실패() throws InterruptedException {
        //Given
        int threadCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Item item = itemRepository.findById(2L).get();
        long bef = item.getViews();

        //When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    itemService.findBulletinItem(2L);
                } finally {
                    // 스레드 작업 완료시 latch 값을 감소시킴
                    latch.countDown();
                }
            });
        }
        latch.await(); //다른 쓰레드에서 수행중인 작업이 완료될때까지 기다려줌
        //Then
        item = itemRepository.findById(2L).get(); //값이 바뀌었는데 DB에서 안 읽어옴
        long aft = item.getViews();
        assertThat(aft - bef).isNotEqualTo(100);
    }

    @Test
    public void 조회수동시성테스트_성공_단일스레드_롤백성공() throws InterruptedException {
        //Given
        Item item = itemRepository.findById(2L).get();
        long bef = item.getViews();

        //When
        itemService.findBulletinItem(2L);
        itemService.findBulletinItem(2L);
        //Then
        item = itemRepository.findById(2L).get();
        long aft = item.getViews();
        assertThat(aft - bef).isEqualTo(2L);
    }

    @Test
    public void 조회수동시성테스트_실패() throws InterruptedException {
        //뭐지 롤백이 안돼
        //Given
        int threadCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Item item = itemRepository.findById(2L).get();
        long before = item.getViews();

        //When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    item.addViews();
                } finally {
                    // 스레드 작업 완료시 latch 값을 감소시킴
                    latch.countDown();
                }
            });
        }
        latch.await();
        //Then
        long after = item.getViews();
        assertThat(after-before).isNotEqualTo(10000L);
    }
}
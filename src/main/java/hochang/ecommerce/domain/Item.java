package hochang.ecommerce.domain;

import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.util.file.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_views", columnList = "views"))
public class Item extends BaseEntity {
    private static final int ZERO = 0;
    private static final List<String> INSUFFICIENT_COUNT = List.of("재고가 부족합니다. ",
            " 의 현재 보유 수량은 ", "개 입니다.");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(length = 40)
    private String name;

    private int count;

    private long price;

    @Lob
    private String contents;

    @ColumnDefault("0")
    private long views;

    private String uploadFileName;

    private String storeFileName;

    @Builder
    public Item(String name, int count, long price, String contents, String uploadFileName, String storeFileName) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.contents = contents;
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    //비즈니스 메소드
    public void modifyItem(ItemRegistration itemRegistration) {
        this.name = itemRegistration.getName();
        this.count = itemRegistration.getCount();
        this.price = itemRegistration.getPrice();
        this.contents = itemRegistration.getContents();
    }

    public void modifyItem(ItemRegistration itemRegistration, UploadFile uploadFile) {
        modifyItem(itemRegistration);
        this.uploadFileName = uploadFile.getUploadFileName();
        this.storeFileName = uploadFile.getStoreFileName();
    }

    public void reduceCount(int count) {
        int reducedCount = this.count - count;
        if (reducedCount < ZERO) {
            throw new IllegalArgumentException(createExceptionMessage());
        }
        this.count = reducedCount;
    }

    public void addViews() {
        this.views++;
    }

    public void addCount(int count) {
        this.count += count;
    }

    private String createExceptionMessage() {
        return INSUFFICIENT_COUNT.get(0) + this.name + INSUFFICIENT_COUNT.get(1)
                + this.count + INSUFFICIENT_COUNT.get(2);
    }
}


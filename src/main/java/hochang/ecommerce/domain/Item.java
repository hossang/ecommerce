package hochang.ecommerce.domain;

import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.util.file.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int count;

    private long price;

    @Lob
    private String contents;

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
        if (reducedCount < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("재고가 부족합니다. ").append(this.name).append(" 의 현재 보유 수량은 ").append(this.count)
                    .append("개 입니다.");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.count = reducedCount;
    }

    public void addCount(int count) {
        this.count += count;
    }
}

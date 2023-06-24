package hochang.ecommerce.domain;

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
public class Item extends BaseTimeEntity {
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

    //마음에 들지 않아... 매게변수 순서잘못입력하면 ? p52 참고
    public void modifyItem(String name, int count, long price, String contents, String uploadFileName, String storeFileName) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.contents = contents;
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        //modifyItem(Item item) {
        //   this.name = item.getName();
        //   this.count = item.getCount();
        //   this.price = item.getPrice();
        //   this.contents = item.getContents();
        //   this.uploadFileName = item.getUploadFileName();
        //   this.storeFileName = item.getStoreFileName();
        // }
    }

    //비즈니스로직 p44
    public void reduceCount(int count) {
        int reducedCount = this.count - count;
        if (reducedCount < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.count = reducedCount;
    }

    public void addCount(int count) {
        this.count += count;
    }
}

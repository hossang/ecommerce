package hochang.ecommerce.domain;

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

    //마음에 들지 않아... 매게변수 순서잘못입력하면 ?
    public void modifyItemForm(String name, int count, long price, String contents, String uploadFileName, String storeFileName) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.contents = contents;
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}

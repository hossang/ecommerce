package hochang.ecommerce.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String imageUploadFileName;

    private String imageStoreFileName;

    @Builder
    public Content(String imageUploadFileName, String imageStoreFileName) {
        this.imageUploadFileName = imageUploadFileName;
        this.imageStoreFileName = imageStoreFileName;
    }

    public void addItem(Item item) {
        this.item = item;
    }

    public void modifyContents(String imageUploadFileName, String imageStoreFileName) {
        this.imageUploadFileName = imageUploadFileName;
        this.imageStoreFileName = imageStoreFileName;
    }

}

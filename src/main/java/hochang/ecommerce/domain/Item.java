package hochang.ecommerce.domain;

import hochang.ecommerce.dto.ItemRegistration;
import hochang.ecommerce.exception.ItemIllegalArgumentException;
import hochang.ecommerce.util.file.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static hochang.ecommerce.constants.NumberConstants.*;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_views", columnList = "views"))
public class Item extends BaseEntity {
    private static final List<String> INSUFFICIENT_STOCK_MESSAGES = List.of("재고가 부족합니다. ",
            " 의 현재 보유 수량은 ", "개 입니다.");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @Column(length = INT_40)
    private String name;

    private int quantity;

    private long price;

    @ColumnDefault("0")
    private long views;

    private String thumbnailUploadFileName;

    private String thumbnailStoreFileName;

    @Builder
    public Item(Account account, String name, int quantity, long price, String thumbnailUploadFileName,
                String thumbnailStoreFileName) {
        this.account = account;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.thumbnailUploadFileName = thumbnailUploadFileName;
        this.thumbnailStoreFileName = thumbnailStoreFileName;
    }

    public void addContent(Content content) {
        contents.add(content);
        content.addItem(this);
    }

    public void modifyItem(int quantity, String thumbnailUploadFileName,
                           String thumbnailStoreFileName, Account account) {
        this.quantity = quantity;
        this.thumbnailUploadFileName = thumbnailUploadFileName;
        this.thumbnailStoreFileName = thumbnailStoreFileName;
        this.account = account;
    }

    public void reduceQuantity(int quantity) {
        int reducedQuantity = this.quantity - quantity;
        if (reducedQuantity < LONG_0) {
            throw new ItemIllegalArgumentException(createExceptionMessage());
        }
        this.quantity = reducedQuantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public String createExceptionMessage() {
        return INSUFFICIENT_STOCK_MESSAGES.get(INT_0) + this.name + INSUFFICIENT_STOCK_MESSAGES.get(INT_1)
                + this.quantity + INSUFFICIENT_STOCK_MESSAGES.get(INT_2);
    }
}


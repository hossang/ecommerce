package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulletinItem {
    private Long id;

    private String name;

    private long price;

    private String contents;

    private String storeFileName;
}

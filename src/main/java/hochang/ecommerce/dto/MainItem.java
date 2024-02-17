package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItem {
    private Long id;

    private String name;

    private long price;

    private String thumbnailStoreFileName;
}

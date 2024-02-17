package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BulletinItem {
    private Long id;

    private String name;

    private long price;

    private String thumbnailStoreFileName;

    private List<String> imageStoreFileNames = new ArrayList<>();
}

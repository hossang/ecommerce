package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BulletinItem {
    private Long id;

    private String name;

    private long price;

    private String contents;

    private String storeFileName;
}

package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemRegistrationForm {
    private Long id;

    private String name;

    private int count;

    private long price;

    private String contents;

    private MultipartFile imageFile;
}

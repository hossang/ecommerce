package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemRegistrationForm {
    @Size(max = 20, message = "제목은 20자 이내이어야 합니다")
    private String name;

    private int count;

    private int price;

    private String contents;

    @Pattern(regexp = "^(image/jpeg|image/png)$", message = "JPG 또는 PNG 이미지만을 사용해야합니다.")
    private MultipartFile imageFile;
}

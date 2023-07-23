package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemRegistration {
    private Long id;

    @Size(max = 40, message = "상품명은 40글자이내여야 합니다.")
    private String name;

    @Min(value = 1, message = "수량은 1개이상이어야 합니다.")
    private int count;

    @NotNull(message = "가격은 필수 입력해야합니다.")
    private long price;

    @NotBlank(message = "상품 내용은 필수 입력해야합니다.")
    private String contents;

    private MultipartFile imageFile;
}

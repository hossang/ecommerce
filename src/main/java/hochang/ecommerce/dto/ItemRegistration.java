package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static hochang.ecommerce.constants.NumberConstants.*;

@Getter
@Setter
public class ItemRegistration {
    private Long id;

    @Size(max = INT_40, message = "상품명은 40글자이내여야 합니다.")
    private String name;

    @Min(value = INT_1, message = "수량은 1개이상이어야 합니다.")
    private int quantity;

    @NotNull(message = "가격은 필수 입력해야합니다.")
    private long price;

    private MultipartFile thumbnailImage;

    private List<MultipartFile> contentImages = new ArrayList<>();

    @NotNull
    private Long accountId;
}

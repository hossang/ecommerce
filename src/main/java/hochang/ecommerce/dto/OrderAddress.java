package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OrderAddress {
    private Long id;

    @NotBlank
    private String postCode;

    @NotBlank
    private String address;

    @NotBlank
    private String detailAddress;
}

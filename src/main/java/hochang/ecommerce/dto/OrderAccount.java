package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OrderAccount {
    private Long id;

    @NotBlank
    private String bank;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private long balance;

    @NotBlank
    private String accountHolder;
}

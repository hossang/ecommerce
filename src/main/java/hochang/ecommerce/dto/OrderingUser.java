package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderingUser {
    private Long shippingAddressId;

    private String fullAddress;

    private Long accountId;

    private String fullAccount;
}

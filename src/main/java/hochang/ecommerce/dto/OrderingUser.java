package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderingUser {
    private Long shippingAddressId;

    private String fullAddress;

    private Long accountId;

    private String fullAccount;
}

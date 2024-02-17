package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {
    private Long itemId;

    private String name;

    private int quantity;

    private long price;

    private long orderPrice;

    private String storeFileName;
}

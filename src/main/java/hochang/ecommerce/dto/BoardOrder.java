package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardOrder {
    private Long id;

    private String orderLineNames;

    private String status;

    private long totalPrice;

    private LocalDateTime createDate;
}

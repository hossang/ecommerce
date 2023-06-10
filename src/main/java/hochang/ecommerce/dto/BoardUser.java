package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardUser {
    private Long id;

    private String username;

    private LocalDateTime createdDate;
}

package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardItem {
    private Long id;

    private String name;

    private LocalDateTime createdDate;

    private long views;
}

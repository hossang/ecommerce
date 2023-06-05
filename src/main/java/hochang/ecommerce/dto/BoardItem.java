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

    //조회수도 볼수 있으면 좋을듯
}

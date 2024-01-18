package hochang.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemSearch {
    private String criteria;
    private String searchQuery;
}

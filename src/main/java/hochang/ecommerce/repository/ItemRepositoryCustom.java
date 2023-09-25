package hochang.ecommerce.repository;

import hochang.ecommerce.dto.MainItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<MainItem> findMainItemsWithCoveringIndex(Pageable pageable);
}

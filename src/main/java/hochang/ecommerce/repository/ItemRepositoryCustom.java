package hochang.ecommerce.repository;

import hochang.ecommerce.dto.ItemSearch;
import hochang.ecommerce.dto.MainItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

public interface ItemRepositoryCustom {

    Page<MainItem> findMainItemsWithCoveringIndex(Pageable pageable);

    Page<MainItem> findSearchedMainItems(Pageable pageable, ItemSearch itemSearch);
}

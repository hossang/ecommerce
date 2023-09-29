package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Modifying
    @Query("update Item i set i.views = i.views + :increment where i.id = :itemId")
    void incrementViewsById(@Param("itemId") Long itemId, @Param("increment") Long increment);

    Page<Item> findAll(Pageable pageable);
}

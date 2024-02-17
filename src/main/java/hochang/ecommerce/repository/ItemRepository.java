package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Account;
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

    @Modifying
    @Query("update Item i set i.quantity = :quantity, i.thumbnailUploadFileName = :thumbnailUploadFileName," +
            " i.thumbnailStoreFileName = :thumbnailStoreFileName, i.account = :account where i.id = :itemId")
    void modifyItem(@Param("itemId") Long itemId, @Param("quantity") int quantity,
                    @Param("thumbnailUploadFileName") String thumbnailUploadFileName,
                    @Param("thumbnailStoreFileName") String thumbnailStoreFileName, @Param("account") Account account);

    Page<Item> findAll(Pageable pageable);
}

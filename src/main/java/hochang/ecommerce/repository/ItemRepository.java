package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.Item;
import org.hibernate.LockMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Modifying
    @Query("update Item i set i.views = i.views + :increment where i.id = :itemId")
    void incrementViewsById(@Param("itemId") Long itemId, @Param("increment") Long increment);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id =:id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);

    Page<Item> findAll(Pageable pageable);
}

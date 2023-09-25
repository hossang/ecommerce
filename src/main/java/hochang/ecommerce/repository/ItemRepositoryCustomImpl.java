package hochang.ecommerce.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hochang.ecommerce.domain.QItem;
import hochang.ecommerce.dto.MainItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MainItem> findMainItemsWithCoveringIndex(Pageable pageable) {
        QItem item = QItem.item;
        List<Long> ids = jpaQueryFactory
                .select(item.id)
                .from(item)
                .orderBy(item.views.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        List<MainItem> mainItems = jpaQueryFactory
                .select(Projections.fields(MainItem.class,
                        item.id,
                        item.name,
                        item.price,
                        item.storeFileName))
                .from(item)
                .where(item.id.in(ids))
                .orderBy(item.id.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(item.id.count())
                .from(item)
                .fetchOne();

        return new PageImpl<>(mainItems, pageable, total);

    }
}

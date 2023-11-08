package hochang.ecommerce.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hochang.ecommerce.domain.QItem;
import hochang.ecommerce.dto.MainItem;
import hochang.ecommerce.util.serialization.PageImplDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MainItem> findMainItemsWithCoveringIndex(Pageable pageable) {
        QItem item = QItem.item;

        List<Long> ids = getIdsWithCoveringIndex(pageable, item);
        List<MainItem> mainItems = getMainItemsWithIds(item, ids);
        JPAQuery<Long> total = getTotal(item);
        //return PageableExecutionUtils.getPage(mainItems, pageable, total::fetchOne);
        return new PageImplDeserializer<>(mainItems, pageable, total.fetchOne());
    }

    private JPAQuery<Long> getTotal(QItem item) {
        return jpaQueryFactory
                .select(item.id.count())
                .from(item);
    }

    private List<MainItem> getMainItemsWithIds(QItem item, List<Long> ids) {
        return jpaQueryFactory
                .select(Projections.fields(MainItem.class,
                        item.id,
                        item.name,
                        item.price,
                        item.storeFileName))
                .from(item)
                .where(item.id.in(ids))
                .orderBy(item.id.desc())
                .fetch();
    }

    private List<Long> getIdsWithCoveringIndex(Pageable pageable, QItem item) {
        return jpaQueryFactory
                .select(item.id)
                .from(item)
                .orderBy(item.views.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
}

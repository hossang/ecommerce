package hochang.ecommerce.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findOrdersWithCoveringIndex(Long userId, List<OrderStatus> orderStatuses, Pageable pageable) {
        QOrder order = QOrder.order;
        BooleanExpression eqBooleanExpression = order.user.id.eq(userId);
        BooleanExpression inBbooleanExpression = order.status.in(orderStatuses);
        BooleanExpression andBooleanExpression = eqBooleanExpression.and(inBbooleanExpression);

        List<Long> ids = jpaQueryFactory
                .select(order.id)
                .from(order)
                .where(andBooleanExpression)
                .orderBy(order.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        List<Order> orders = jpaQueryFactory
                .selectFrom(order)
                .where(order.id.in(ids))
                .orderBy(order.id.desc())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(order)
                .where(andBooleanExpression)
                .fetch()
                .size();

        return new PageImpl<>(orders, pageable, total);
    }
}

package hochang.ecommerce.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hochang.ecommerce.domain.Order;
import hochang.ecommerce.domain.OrderStatus;
import hochang.ecommerce.domain.QOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findOrdersWithCoveringIndex(Long userId, List<OrderStatus> orderStatuses, Pageable pageable) {
        QOrder order = QOrder.order;
        BooleanExpression eqBooleanExpression = order.user.id.eq(userId);
        BooleanExpression inBbooleanExpression = order.status.in(orderStatuses);
        BooleanExpression andBooleanExpression = eqBooleanExpression.and(inBbooleanExpression);

        List<Long> ids = getIdsWithCoveringIndex(pageable, order, andBooleanExpression);
        List<Order> orders = getOrdersWithIds(order, ids);
        JPAQuery<Long> total = getTotal(order, andBooleanExpression);

        return PageableExecutionUtils.getPage(orders, pageable, total::fetchOne);
    }

    private JPAQuery<Long> getTotal(QOrder order, BooleanExpression andBooleanExpression) {
        return jpaQueryFactory
                .select(order.id.count())
                .from(order)
                .where(andBooleanExpression);
    }

    private List<Order> getOrdersWithIds(QOrder order, List<Long> ids) {
        return jpaQueryFactory
                .selectFrom(order)
                .where(order.id.in(ids))
                .orderBy(order.id.desc())
                .fetch();
    }

    private List<Long> getIdsWithCoveringIndex(Pageable pageable, QOrder order, BooleanExpression andBooleanExpression) {
        return jpaQueryFactory
                .select(order.id)
                .from(order)
                .where(andBooleanExpression)
                .orderBy(order.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }
}

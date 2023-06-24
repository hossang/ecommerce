package hochang.ecommerce.dto;

import hochang.ecommerce.domain.OrderStatus;

import java.util.EnumMap;
import java.util.Map;

public final class OrderStatusConstants {
    public static final Map<OrderStatus, String> ORDER_STATUS_MAP = new EnumMap<>(OrderStatus.class);
    static {
        ORDER_STATUS_MAP.put(OrderStatus.ORDER, "주문중");
        ORDER_STATUS_MAP.put(OrderStatus.CANCEL, "주문 취소");
        ORDER_STATUS_MAP.put(OrderStatus.COMPLETE, "주문 완료");
    }

    private OrderStatusConstants(){}
}

package hochang.ecommerce.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static hochang.ecommerce.constants.NumberConstants.*;

@Entity
@Getter
@Table(name = "orders", indexes = @Index(name = "idx_user_status", columnList = "users_id, status"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    private static final long ZERO_PRICE = 0L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shippingAddress_id")
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = INT_10)
    private OrderStatus status;

    private long totalPrice;

    @Builder
    public Order(User user, ShippingAddress shippingAddress, OrderLine orderLine) {
        this.user = user;
        this.shippingAddress = shippingAddress;
        addOrderLine(orderLine);
        this.status = OrderStatus.ORDER;
        calculateTotalPrice();
    }

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.addOrder(this);
    }

    public void linkForeignEntity(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void calculateTotalPrice() {
        this.totalPrice = ZERO_PRICE;
        for (OrderLine orderLine : orderLines) {
            this.totalPrice += orderLine.getOrderPrice();
        }
    }

    public void completeOrder() {
        this.status = OrderStatus.COMPLETE;
    }

    public void cancelOrder() {
        this.status = OrderStatus.CANCEL;
        restoreItem();
    }

    public void restoreItem() {
        for (OrderLine orderLine : orderLines) {
            orderLine.getItem().addCount(orderLine.getCount());
        }
    }
}

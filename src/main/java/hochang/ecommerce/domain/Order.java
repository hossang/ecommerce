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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private long totalPrice;

    @Builder
    public Order(User user, OrderLine orderLine) {
        this.user = user;
        addOrderLine(orderLine);
        this.status = OrderStatus.ORDER;
        calculateTotalPrice();
    }

    //연관관계 메서드
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.addOrder(this);
    }

    //비즈니스 메소드
    public void calculateTotalPrice() {
        this.totalPrice = 0L;
        for (OrderLine orderLine : orderLines) {
            this.totalPrice += orderLine.getOrderPrice();
        }
    }

    public void completeOrder() {
        this.status = OrderStatus.COMPLETE;
        for (OrderLine orderLine : orderLines) {
            orderLine.getItem().reduceCount(orderLine.getCount());
        }
    }

    public void cancelOrder() {
        this.status = OrderStatus.CANCEL;
        for (OrderLine orderLine : orderLines) {
            orderLine.getItem().addCount(orderLine.getCount());
        }
    }
}

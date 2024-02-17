package hochang.ecommerce.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine extends BaseEntity {
    private static final long ZERO_PRICE = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderLine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    private long orderPrice;

    @Builder
    public OrderLine(Item item, int quantity) {
        this.item = item;
        this.item.reduceQuantity(quantity);
        this.quantity = quantity;
        calculateOrderPrice();
    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public void modifyCount(int quantity) {
        this.item.reduceQuantity(quantity);
        this.quantity += quantity;
        calculateOrderPrice();
    }

    private void calculateOrderPrice() {
        this.orderPrice = item.getPrice() * quantity;
    }
}

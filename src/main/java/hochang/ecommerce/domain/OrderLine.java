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
public class OrderLine extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderLine_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int count;

    private long orderPrice;


    @Builder
    public OrderLine(Item item, int count) {
        this.item = item;
        this.item.reduceCount(count);
        this.count = count;
        calculateOrderPrice();
    }

    //
    public void addOrder(Order order) {
        this.order = order;
    }

    public void modifyCount(int count) {
        this.item.reduceCount(count);
        this.count += count;
        calculateOrderPrice();
    }

    private void calculateOrderPrice() {
        this.orderPrice = 0L;
        this.orderPrice = item.getPrice() * count;
    }
}

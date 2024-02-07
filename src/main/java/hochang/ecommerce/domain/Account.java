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

import static hochang.ecommerce.constants.NumberConstants.INT_10;
import static hochang.ecommerce.constants.NumberConstants.INT_40;
import static hochang.ecommerce.constants.NumberConstants.LONG_0;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {
    private static final String EXCEPTION_MESSAGE = "잔고가 부족합니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(length = INT_10)
    private String bank;

    @Column(length = INT_40)
    private String accountNumber;

    private long balance;

    @Column(length = INT_10)
    private String accountHolder;

    @Builder
    public Account(User user, String bank, String accountNumber, long balance, String accountHolder) {
        this.user = user;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountHolder = accountHolder;
    }

    public void pay(long amount) {
        long reducedBalance = this.balance - amount;
        if (reducedBalance < LONG_0) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE);
        }
        this.balance = reducedBalance;

    }

    public void refund(long amount) {
        this.balance += amount;

    }
}

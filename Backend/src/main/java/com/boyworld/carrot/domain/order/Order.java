package com.boyworld.carrot.domain.order;

import com.boyworld.carrot.domain.TimeBaseEntity;
import com.boyworld.carrot.domain.member.Member;
import com.boyworld.carrot.domain.sale.Sale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 엔티티
 *
 * @author 박은규
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false)
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Status status;

    @Column
    private LocalDateTime expectTime;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Boolean active;

    @Builder
    private Order(Member member, Sale sale, Status status, LocalDateTime expectTime,
        Integer totalPrice, Boolean active) {
        this.member = member;
        this.sale = sale;
        this.status = status;
        this.expectTime = expectTime;
        this.totalPrice = totalPrice;
        this.active = active;
    }

    // == business logic == //
    public Order editOrderStatus(Status status) {
        this.status = status;

        return this;
    }

    public Order editOrderStatusAndExpectTime(Status status, LocalDateTime expectTime) {
        this.status = status;
        this.expectTime = expectTime;

        return this;
    }

    public Order editOrderActive(Boolean active) {
        this.active = active;

        return this;
    }

    // == business logic ==//
    public void editOrderTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}

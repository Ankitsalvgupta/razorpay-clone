package com.ankitgupta.razorpay.payment.entity;

import com.ankitgupta.razorpay.common.entity.Money;
import com.ankitgupta.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record")
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //no relationship with merchant table FK -- cross service boundary/ communication
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    private Integer attempts;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}

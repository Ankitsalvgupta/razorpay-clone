package com.ankitgupta.razorpay.operations.entity;

import com.ankitgupta.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "settlement_payment")
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}

package com.ankitgupta.razorpay.payment.entity;

import com.ankitgupta.razorpay.common.enums.PaymentActor;
import com.ankitgupta.razorpay.common.enums.PaymentEvent;
import com.ankitgupta.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor", length = 100)
    private PaymentActor actor;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}

package com.ankitgupta.razorpay.operations.repository;

import com.ankitgupta.razorpay.operations.entity.SettlementPayment;
import com.ankitgupta.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
}

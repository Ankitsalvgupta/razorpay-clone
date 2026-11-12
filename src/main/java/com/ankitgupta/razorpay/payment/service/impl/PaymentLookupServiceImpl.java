package com.ankitgupta.razorpay.payment.service.impl;

import com.ankitgupta.razorpay.common.enums.PaymentStatus;
import com.ankitgupta.razorpay.payment.api.PaymentLookupService;
import com.ankitgupta.razorpay.payment.entity.Payment;
import com.ankitgupta.razorpay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentLookupServiceImpl implements PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findUnsettledCapturedPayments(UUID merchantId) {
        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
    }
}

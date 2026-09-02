package com.ankitgupta.razorpay.payment.service;

import com.ankitgupta.razorpay.payment.dto.request.PaymentInitRequest;
import com.ankitgupta.razorpay.payment.dto.response.PaymentResponse;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);
}

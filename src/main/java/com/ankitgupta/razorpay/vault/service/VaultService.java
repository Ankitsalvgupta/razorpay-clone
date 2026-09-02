package com.ankitgupta.razorpay.vault.service;

import com.ankitgupta.razorpay.common.entity.Money;
import com.ankitgupta.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.ankitgupta.razorpay.vault.dto.request.TokenizeRequest;
import com.ankitgupta.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}

package com.ankitgupta.razorpay.payment.service;

import com.ankitgupta.razorpay.payment.dto.request.CreateOrderRequest;
import com.ankitgupta.razorpay.payment.dto.response.OrderResponse;
import com.ankitgupta.razorpay.payment.dto.response.PaymentResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);

    OrderResponse getById(UUID merchantId, UUID orderId);

    OrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}

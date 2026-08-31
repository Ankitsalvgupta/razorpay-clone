package com.ankitgupta.razorpay.payment.service;

import com.ankitgupta.razorpay.payment.dto.request.CreateOrderRequest;
import com.ankitgupta.razorpay.payment.dto.response.OrderResponse;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}

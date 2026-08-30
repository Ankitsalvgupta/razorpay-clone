package com.ankitgupta.razorpay.merchant.service;

import com.ankitgupta.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ankitgupta.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    @Nullable MerchantResponse signup(MerchantSignupRequest request);
}

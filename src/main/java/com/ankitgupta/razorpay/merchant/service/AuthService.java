package com.ankitgupta.razorpay.merchant.service;

import com.ankitgupta.razorpay.merchant.dto.request.LoginRequest;
import com.ankitgupta.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ankitgupta.razorpay.merchant.dto.response.LoginResponse;
import com.ankitgupta.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}

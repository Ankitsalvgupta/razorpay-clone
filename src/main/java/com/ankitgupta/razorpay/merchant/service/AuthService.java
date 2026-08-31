package com.ankitgupta.razorpay.merchant.service;

import com.ankitgupta.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ankitgupta.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}

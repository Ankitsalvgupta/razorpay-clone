package com.ankitgupta.razorpay.merchant.service;

import com.ankitgupta.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ankitgupta.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface ApiKeyService {

    @Nullable ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);
}

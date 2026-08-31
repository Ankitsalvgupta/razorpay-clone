package com.ankitgupta.razorpay.merchant.service;

import com.ankitgupta.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ankitgupta.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.ankitgupta.razorpay.merchant.dto.response.ApiKeyResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}

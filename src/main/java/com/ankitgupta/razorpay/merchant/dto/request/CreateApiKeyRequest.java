package com.ankitgupta.razorpay.merchant.dto.request;

import com.ankitgupta.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}

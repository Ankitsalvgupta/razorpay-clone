package com.ankitgupta.razorpay.vault.dto.response;

import com.ankitgupta.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}

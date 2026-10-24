package com.ankitgupta.razorpay.merchant.api;

import com.ankitgupta.razorpay.common.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {

    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);
}

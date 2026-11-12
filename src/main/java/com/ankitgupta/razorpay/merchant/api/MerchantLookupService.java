package com.ankitgupta.razorpay.merchant.api;

import com.ankitgupta.razorpay.common.dto.SettlementBankDetails;
import com.ankitgupta.razorpay.common.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantLookupService {

    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);

    List<UUID> listActiveMerchantIds();

    SettlementBankDetails getSettlementBankDetails(UUID merchantId);
}

package com.ankitgupta.razorpay.merchant.service.impl;

import com.ankitgupta.razorpay.common.dto.SettlementBankDetails;
import com.ankitgupta.razorpay.common.dto.WebhookTarget;
import com.ankitgupta.razorpay.common.enums.MerchantStatus;
import com.ankitgupta.razorpay.common.exception.ResourceNotFoundException;
import com.ankitgupta.razorpay.merchant.api.MerchantLookupService;
import com.ankitgupta.razorpay.merchant.entity.Merchant;
import com.ankitgupta.razorpay.merchant.repository.MerchantRepository;
import com.ankitgupta.razorpay.merchant.repository.WebhookConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantLookupServiceImpl implements MerchantLookupService {

    private final MerchantRepository merchantRepository;
    private final WebhookConfigRepository merchantWebhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {
        return merchantWebhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId).stream()
                .filter(config -> config.isSubscribedTo(eventType))
                .map(config -> {
                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();
    }

    @Override
    public List<UUID> listActiveMerchantIds() {
        return merchantRepository.findByStatus(MerchantStatus.ACTIVE)
                .stream().map(m -> m.getId()).toList();
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant ", merchantId));

        return new SettlementBankDetails(
                merchant.getSettlementBankAccount(),
                merchant.getSettlementBankIfsc(),
                merchant.getSettlementBankAccountHolderName()
        );
    }
}

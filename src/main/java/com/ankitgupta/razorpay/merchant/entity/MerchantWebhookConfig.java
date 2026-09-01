package com.ankitgupta.razorpay.merchant.entity;

import com.ankitgupta.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
    indexes = {
        @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id, enabled")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    //Merchant's website URL where the webhook will be sent
    @Column(nullable = false, length = 500)
    private String targetUrl;   // e.g., www.merchantwebsite.com/webhook/success || www.zara.com/webhook/success

    @Column(length = 255)
    private String webhookSecretHash;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(length = 255)
    private String eventTypes;  // Comma-separated list of event types to which the webhook is subscribed
    // if eventTypes is null or empty, it means the webhook is subscribed to all event types
}

package com.ankitgupta.razorpay.merchant.entity;

import com.ankitgupta.razorpay.common.entity.BaseEntity;
import com.ankitgupta.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key",
    indexes = {
        @Index(name = "idx_api_key_merchant_env", columnList = "merchant_id, environment, enabled")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 50, unique = true)
    private String keyId;

    @Column(nullable = false, length = 200)
    private String keySecretHash;

    @Column(length = 200)
    private String previousKeySecretHash;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Environment environment; // "sandbox" or "production"

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime lastUsedAt;

    private LocalDateTime rotatedAt;

    private LocalDateTime gracePeriodExpiresAt;

    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}

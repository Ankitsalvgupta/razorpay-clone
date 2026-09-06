package com.ankitgupta.razorpay.vault.entity;

import com.ankitgupta.razorpay.common.entity.BaseEntity;
import com.ankitgupta.razorpay.common.enums.CardBrand;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaultCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 4)
    private String lastFour;

    // The BIN (Bank Identification Number) is the first 6 digits of the PAN (Primary Account Number)
    @Column(nullable = false, length = 6)
    private String bin;

    // The PAN (Primary Account Number), encrypted using the DEK (Data Encryption Key)
    @Column(nullable = false)
    private byte[] encryptedPan;

    /**
     * The DEK used to encrypt the PAN above, itself encrypted using a master/KEK
     * (Key Encryption Key) — this is envelope encryption
     */
    @Column(nullable = false)
    private byte[] encryptedDek;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardBrand brand; // e.g. VISA, MASTERCARD, AMEX, etc.

    @Column(nullable = false)
    private String expiryMonth;

    @Column(nullable = false)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;
}

package com.ankitgupta.razorpay.merchant.repository;

import com.ankitgupta.razorpay.common.enums.MerchantStatus;
import com.ankitgupta.razorpay.merchant.entity.Merchant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    boolean existsByEmail(String email);

    List<Merchant> findByStatus(MerchantStatus merchantStatus);
}

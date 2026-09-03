package com.ankitgupta.razorpay.vault.controller;

import com.ankitgupta.razorpay.vault.dto.request.TokenizeRequest;
import com.ankitgupta.razorpay.vault.dto.response.TokenizeResponse;
import com.ankitgupta.razorpay.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/vault")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    UUID merchantId = UUID.fromString("09081161-345c-4067-a2a7-adcc25f053b9");  //TODO: replace it with MerchantContext

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vaultService.tokenize(request, merchantId));
    }
}

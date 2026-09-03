package com.ankitgupta.razorpay.merchant.controller;

import com.ankitgupta.razorpay.merchant.dto.request.LoginRequest;
import com.ankitgupta.razorpay.merchant.dto.response.LoginResponse;
import com.ankitgupta.razorpay.merchant.dto.response.MerchantResponse;
import com.ankitgupta.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ankitgupta.razorpay.merchant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid MerchantSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                authService.signup(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                authService.login(request)
        );
    }
}

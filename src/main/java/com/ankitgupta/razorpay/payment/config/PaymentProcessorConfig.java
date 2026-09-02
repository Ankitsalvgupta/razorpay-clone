package com.ankitgupta.razorpay.payment.config;

import com.ankitgupta.razorpay.common.enums.PaymentMethod;
import com.ankitgupta.razorpay.payment.processor.PaymentProcessor;
import com.ankitgupta.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.ankitgupta.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.ankitgupta.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentMethodPaymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}

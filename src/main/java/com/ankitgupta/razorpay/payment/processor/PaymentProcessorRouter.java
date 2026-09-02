package com.ankitgupta.razorpay.payment.processor;

import com.ankitgupta.razorpay.common.enums.PaymentMethod;
import com.ankitgupta.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ankitgupta.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor processor = paymentProcessors.get(request.method());
        if(processor == null) {
            throw new IllegalArgumentException("No payment processor registered for method: " + request.method());
        }
        return processor.charge(request);
    }
}

package com.ankitgupta.razorpay.payment.processor;

import com.ankitgupta.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.ankitgupta.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}

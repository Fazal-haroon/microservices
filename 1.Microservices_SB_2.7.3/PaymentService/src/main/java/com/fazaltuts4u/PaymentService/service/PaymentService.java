package com.fazaltuts4u.PaymentService.service;

import com.fazaltuts4u.PaymentService.model.PaymentRequest;
import com.fazaltuts4u.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}

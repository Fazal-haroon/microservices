package com.fazaltuts4u.PaymentService.service;

import com.fazaltuts4u.PaymentService.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}

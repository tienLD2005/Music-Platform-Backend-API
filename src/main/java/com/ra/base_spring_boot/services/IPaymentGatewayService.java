package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Payment;

public interface IPaymentGatewayService{
    String initiatePayment(Payment payment);
    boolean verifyPayment(String transactionId);
}

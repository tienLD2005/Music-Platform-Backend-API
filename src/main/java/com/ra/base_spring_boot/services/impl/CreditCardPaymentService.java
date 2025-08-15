package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.services.IPaymentGatewayService;
import org.springframework.stereotype.Service;

@Service("CREDIT_CARD")
public class CreditCardPaymentService implements IPaymentGatewayService{

    @Override
    public String initiatePayment(Payment payment) {
        return "https://sandbox.stripe.com/pay?transactionId=" + payment.getId();
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        return true;
    }
}

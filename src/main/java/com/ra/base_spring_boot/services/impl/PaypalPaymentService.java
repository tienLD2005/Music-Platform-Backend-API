package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.services.IPaymentGatewayService;
import org.springframework.stereotype.Service;

@Service("PAYPAL")
public class PaypalPaymentService implements IPaymentGatewayService{

    @Override
    public String initiatePayment(Payment payment) {
        return "https://sandbox.paypal.com/checkout?transactionId=" + payment.getId();
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        return true;
    }
}

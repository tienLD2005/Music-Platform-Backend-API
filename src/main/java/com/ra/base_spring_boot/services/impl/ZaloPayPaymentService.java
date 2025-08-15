package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.services.IPaymentGatewayService;
import org.springframework.stereotype.Service;

@Service("ZALO_PAY")
public class ZaloPayPaymentService implements IPaymentGatewayService{

    @Override
    public String initiatePayment(Payment payment) {
        return "https://sandbox.zalopay.vn/pay?transactionId=" + payment.getId();
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        return true;
    }
}

package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.services.IPaymentGatewayService;
import com.ra.base_spring_boot.services.paymentgateway.PaymentGatewayFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentGatewayFactory gatewayFactory;
    private final IPaymentRepository paymentRepository;

    public String createPayment(Payment payment) {
        IPaymentGatewayService gateway = gatewayFactory.getGateway(payment.getPaymentMethod());
        return gateway.initiatePayment(payment);
    }

    public void handleCallback(String transactionId, PaymentMethod method) {
        IPaymentGatewayService gateway = gatewayFactory.getGateway(method);
        boolean verified = gateway.verifyPayment(transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (verified) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
    }
}

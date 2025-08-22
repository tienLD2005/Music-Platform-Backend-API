package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.services.IPaymentProcessor;
import com.ra.base_spring_boot.services.paypal.PaypalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalProcessor implements IPaymentProcessor{

    private final PaypalService paypalService;
    private final IPaymentRepository paymentRepository;

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.PAYPAL;
    }

    @Override
    public String createPayment(User user, SubscriptionPlan plan) {
        Map<String, String> orderResponse = paypalService.createOrder(
                BigDecimal.valueOf(plan.getPrice()), "USD"
        );

        Payment payment = Payment.builder()
                .user(user)
                .subscriptionPlan(plan)
                .amount(plan.getPrice())
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(orderResponse.get("orderId"))
                .build();

        paymentRepository.save(payment);
        return orderResponse.get("approvalUrl");
    }

    @Override
    public Map<String, Object> capturePayment(String orderId, Payment payment) {
        return paypalService.captureOrder(orderId);
    }
}
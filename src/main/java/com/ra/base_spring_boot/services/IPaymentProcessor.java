package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentMethod;

import java.util.Map;

public interface IPaymentProcessor{
    boolean supports(PaymentMethod method);

    String createPayment(User user, SubscriptionPlan plan);

    Map<String, Object> capturePayment(String orderId, Payment payment);
}
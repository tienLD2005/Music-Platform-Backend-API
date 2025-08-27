package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.constants.PaymentMethod;

import java.util.List;

public interface IClientPaymentService {
    List<PaymentResponseDTO> getPaymentsHistory();

    String createPayment(SubscriptionRequestDTO requestDTO);

    SubscriptionResponseDTO capturePayment(String orderId, Long subscriptionId, PaymentMethod method);

    SubscriptionResponseDTO processPaypalSuccess(String token, String payerId);
    void processPaypalCancel(String token);
    Long getPaymentIdByToken(String token);
}

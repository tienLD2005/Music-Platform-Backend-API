package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.Subscription;

public class PaymentMapper {
    public static PaymentResponseDTO toPaymentResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    public static SubscriptionResponseDTO toSubscriptionResponse(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .planName(subscription.getPlan_id().getPlanName())
                .price(subscription.getPlan_id().getPrice())
                .startTime(subscription.getStartTime())
                .endTime(subscription.getEndTime())
                .status(subscription.getStatus().name())
                .build();
    }
}

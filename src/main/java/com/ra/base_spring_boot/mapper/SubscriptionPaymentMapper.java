package com.ra.base_spring_boot.mapper;

import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.Subscription;

public class SubscriptionPaymentMapper{
    public static SubscriptionResponseDTO mapToResponseDTO(Subscription subscription) {
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

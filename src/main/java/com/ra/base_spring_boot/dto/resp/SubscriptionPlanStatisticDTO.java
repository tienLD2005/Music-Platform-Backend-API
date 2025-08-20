package com.ra.base_spring_boot.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanStatisticDTO {
    private Long planId;
    private String planName;
    private Long totalSubscriptions;
    private Long totalUsers;
    private Double totalRevenue;
}

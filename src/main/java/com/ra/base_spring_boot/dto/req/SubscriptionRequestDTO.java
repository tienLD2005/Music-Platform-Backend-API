package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {
    @NotNull(message = "Plan id can't be null")
    private Long planId;

    @NotNull(message = "Please select payment method")
    private PaymentMethod paymentMethod;
}
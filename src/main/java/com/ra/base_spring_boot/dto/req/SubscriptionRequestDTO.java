package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {
    @NotNull(message = "Plan id can't null")
    private Long planId;

    @Pattern(
            regexp = "^(PAYPAL|CREDIT_CARD|MOMO|ZALO_PAY)$",
            message = "Invalid payment method"
    )
    private String paymentMethod;
}

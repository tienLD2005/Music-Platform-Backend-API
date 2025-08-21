package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequestDTO {
    @NotNull(message = "Plan id can't be null")
    private Long planId;

    @Pattern(
            regexp = "^(PAYPAL|CREDIT_CARD|MOMO|ZALO_PAY)$",
            message = "Payment method must be one of: PAYPAL, CREDIT_CARD, MOMO, ZALO_PAY"
    )
    private String paymentMethod;

}
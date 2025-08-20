package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanRequestDTO {
    @NotBlank(message = "Plan name can't be empty")
    private String planName;
    
    @NotNull(message = "Price can't be empty")
    @Min(value = 0, message = "The price must be >= 0")
    private Double price;
    
    @NotNull(message = "Duration day can't be empty")
    @Min(value = 1, message = "The duration must be >= 1 day")
    private Integer durationDay;
    
    private String description;
}

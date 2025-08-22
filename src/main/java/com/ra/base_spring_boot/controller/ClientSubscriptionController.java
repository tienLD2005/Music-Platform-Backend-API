package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.services.IClientSubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Users Subscriptions")
public class ClientSubscriptionController {

    private final IClientSubscriptionService subscriptionService;

    @GetMapping("/current")
    public ResponseWrapper<SubscriptionResponseDTO> getCurrentSubscription() {
        return ResponseWrapper.<SubscriptionResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(subscriptionService.getCurrentSubscription())
                .build();
    }

    @GetMapping("/history")
    public ResponseWrapper<List<SubscriptionResponseDTO>> getSubscriptionHistory() {
        return ResponseWrapper.<List<SubscriptionResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(subscriptionService.getSubscriptionHistory())
                .build();
    }

    @DeleteMapping("/cancel/{subscriptionId}")
    public ResponseWrapper<SubscriptionResponseDTO> cancelSubscription(
            @PathVariable Long subscriptionId) {
        return ResponseWrapper.<SubscriptionResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(subscriptionService.cancelSubscription(subscriptionId))
                .build();
    }
}
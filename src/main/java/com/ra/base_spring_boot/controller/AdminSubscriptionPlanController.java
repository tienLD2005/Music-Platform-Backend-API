package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SubscriptionPlanRequestDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanResponseDTO;
import com.ra.base_spring_boot.services.ISubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/subscription_plan")
@RequiredArgsConstructor
public class AdminSubscriptionPlanController {
    private final ISubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllSubscriptionPlans(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "") String keyword,
                                                                      @RequestParam(defaultValue = "planName") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String sortDir){

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(subscriptionPlanService.getAll(keyword, page, size, sortBy, sortDir))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<SubscriptionPlanResponseDTO>> addSubscriptionPlan(@RequestBody @Valid SubscriptionPlanRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<SubscriptionPlanResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(subscriptionPlanService.save(request))
                        .build()
        );
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ResponseWrapper<?>> deleteSubscriptionPlan(@PathVariable Long planId) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(subscriptionPlanService.delete(planId))
                        .build()
        );
    }
}

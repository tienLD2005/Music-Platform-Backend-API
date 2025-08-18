package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.SubscriptionPlanRequestDTO;
import com.ra.base_spring_boot.dto.resp.PaginatedResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanResponseDTO;
import com.ra.base_spring_boot.model.base.Pagination;
import com.ra.base_spring_boot.services.ISubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscription_plan")
@RequiredArgsConstructor
public class SubscriptionPlanController {
    private final ISubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<?>> getAllSubscriptionPlans(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "") String keyword,
                                                                      @RequestParam(defaultValue = "planName") String sortBy,
                                                                      @RequestParam(defaultValue = "asc") String sortDir){
        Page<SubscriptionPlanResponseDTO> subscriptionPlans = subscriptionPlanService.getAll(keyword, page, size, sortBy, sortDir);

        PaginatedResponse<SubscriptionPlanResponseDTO> paginated = new PaginatedResponse<>();
        paginated.setItems(subscriptionPlans.getContent());
        paginated.setPagination(new Pagination(
                subscriptionPlans.getNumber() + 1,
                subscriptionPlans.getSize(),
                subscriptionPlans.getTotalPages(),
                subscriptionPlans.getTotalElements()
        ));

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(paginated)
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
        String message = subscriptionPlanService.delete(planId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(message)
                        .build()
        );
    }
}

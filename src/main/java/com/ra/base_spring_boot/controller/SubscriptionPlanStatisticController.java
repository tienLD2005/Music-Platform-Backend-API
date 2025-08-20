package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanStatisticDTO;
import com.ra.base_spring_boot.services.ISubscriptionPlanStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/statistics/plans-statistics")
@RequiredArgsConstructor
public class SubscriptionPlanStatisticController {

    private final ISubscriptionPlanStatisticService statisticService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<SubscriptionPlanStatisticDTO>>> getStatistics(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<SubscriptionPlanStatisticDTO> stats = statisticService.getStatistics(page, size);

        ResponseWrapper<PageResponse<SubscriptionPlanStatisticDTO>> response =
                ResponseWrapper.<PageResponse<SubscriptionPlanStatisticDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(stats)
                        .build();

        return ResponseEntity.ok(response);
    }
}

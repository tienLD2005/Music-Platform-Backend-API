package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.impl.IUserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user-statistics")
@RequiredArgsConstructor
public class UserStatisticsController {

    private final IUserStatisticsService statisticsService;

    @GetMapping("/status")
    public ResponseWrapper<Map<String, Long>> getUserCountByStatus() {
        Map<String, Long> data = statisticsService.getUserCountByStatus();
        return ResponseWrapper.<Map<String, Long>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    @GetMapping("/account-type")
    public ResponseWrapper<Map<String, Long>> getUserCountByAccountType() {
        Map<String, Long> data = statisticsService.getUserCountByAccountType();
        return ResponseWrapper.<Map<String, Long>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }
}

package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.BannerResponse;
import com.ra.base_spring_boot.services.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService bannerService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveBanners(@RequestParam(required = false) String position) {
        List<BannerResponse> banners = bannerService.getActiveBanners(position);

        return ResponseEntity.ok(
                ResponseWrapper.<List<BannerResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(banners)
                        .build()
        );
    }
}


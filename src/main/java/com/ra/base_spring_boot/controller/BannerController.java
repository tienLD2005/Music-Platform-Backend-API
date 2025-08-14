package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.BannerCreateRequest;
import com.ra.base_spring_boot.dto.resp.BannerResponseDTO;
import com.ra.base_spring_boot.dto.resp.BannerResponse;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.constants.BannerStatus;
import com.ra.base_spring_boot.services.IBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<BannerResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<BannerResponseDTO> result = bannerService.getAll(page, size, keyword);

        PageResponse<BannerResponseDTO> pageResponse = PageResponse.<BannerResponseDTO>builder()
                .content(result.getContent())
                .currentPage(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .size(result.getSize())
                .build();

        ResponseWrapper<PageResponse<BannerResponseDTO>> body = ResponseWrapper.<PageResponse<BannerResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(pageResponse)
                .build();

        return ResponseEntity.ok(body);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<BannerResponseDTO>> create(
            @RequestPart String title,
            @RequestPart(required = false) String position,
            @RequestPart(required = false) String startTime,
            @RequestPart(required = false) String endTime,
            @RequestPart MultipartFile image
    ) {
        BannerCreateRequest req = new BannerCreateRequest();
        req.setTitle(title);
        req.setPosition(position);
        if (startTime != null && !startTime.isBlank()) {
            req.setStartTime(LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isBlank()) {
            req.setEndTime(LocalDateTime.parse(endTime));
        }
        req.setStatus(BannerStatus.ACTIVE);
        req.setImage(image);

        BannerResponseDTO created = bannerService.create(req);

        ResponseWrapper<BannerResponseDTO> body = ResponseWrapper.<BannerResponseDTO>builder()
                .status(HttpStatus.CREATED)
                .code(HttpStatus.CREATED.value())
                .data(created)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> delete(@PathVariable Integer id) {
        bannerService.delete(id);
        ResponseWrapper<Void> body = ResponseWrapper.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .code(HttpStatus.NO_CONTENT.value())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(body);
    }

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

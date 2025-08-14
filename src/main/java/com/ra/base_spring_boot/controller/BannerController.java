package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.BannerCreateRequest;
import com.ra.base_spring_boot.dto.req.BannerUpdateReq;
import com.ra.base_spring_boot.dto.req.SearchBannerRequest;
import com.ra.base_spring_boot.dto.resp.BannerResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.mapper.PageMapper;
import com.ra.base_spring_boot.services.IBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService bannerService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<BannerResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BannerResponseDTO> result = bannerService.getAll(page, size);
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<BannerResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(PageMapper.toPageResponse(result))
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchBannerRequest req) {
        int page = (req.getPage() == null || req.getPage() < 0) ? 0 : req.getPage();
        int size = (req.getSize() == null || req.getSize() <= 0) ? 10 : req.getSize();
        Page<BannerResponseDTO> result = bannerService.searchByKeyword(page, size, req.getKeyword());
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<BannerResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(PageMapper.toPageResponse(result))
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<?>> create(
            @Valid @ModelAttribute BannerCreateRequest req
    ) {
        BannerResponseDTO created = bannerService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<BannerResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(created)
                        .build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<?>> update(
            @PathVariable Integer id,
            @ModelAttribute BannerUpdateReq req
    ) {
        BannerResponseDTO updated = bannerService.update(id, req);
        return ResponseEntity.ok(
                ResponseWrapper.<BannerResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(updated)
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Integer id) {
        bannerService.delete(id);
        ResponseWrapper<String> body = ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Banner deleted successfully")
                .build();
        return ResponseEntity.ok(body);
    }
}


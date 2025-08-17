package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.AlbumDeleteRequest;
import com.ra.base_spring_boot.dto.resp.AlbumAdminResponse;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.services.IAlbumAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/albums")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ADMIN')")
public class AlbumAdminController {

    private final IAlbumAdminService albumAdminService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả album",
            description = "Admin xem danh sách album với tìm kiếm và phân trang")
    public ResponseEntity<ResponseWrapper<PageResponse<AlbumAdminResponse>>> getAllAlbums(
            @Parameter(description = "Từ khóa tìm kiếm (tên album hoặc nghệ sĩ)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Trạng thái album")
            @RequestParam(required = false) AlbumStatus status,

            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng item mỗi trang")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sắp xếp theo (title, releaseDate, createdAt, status)")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Hướng sắp xếp (asc, desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<AlbumAdminResponse> result = albumAdminService.getAllAlbums(keyword, status, pageable);

        return ResponseEntity.ok(ResponseWrapper.<PageResponse<AlbumAdminResponse>>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(result)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết album", description = "Xem chi tiết một album")
    public ResponseEntity<ResponseWrapper<AlbumAdminResponse>> getAlbumById(
            @PathVariable Long id) {

        AlbumAdminResponse result = albumAdminService.getAlbumById(id);

        return ResponseEntity.ok(ResponseWrapper.<AlbumAdminResponse>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(result)
                .build());
    }

    @DeleteMapping
    @Operation(summary = "Xóa album",
            description = "Admin xóa album không phù hợp và gửi email thông báo")
    public ResponseEntity<ResponseWrapper<String>> deleteAlbum(
            @Valid @RequestBody AlbumDeleteRequest request) {

        albumAdminService.deleteAlbum(request);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Album đã được xóa thành công và email thông báo đã được gửi đến nghệ sĩ")
                .build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái album",
            description = "Duyệt hoặc từ chối album")
    public ResponseEntity<ResponseWrapper<String>> updateAlbumStatus(
            @PathVariable Long id,
            @RequestParam AlbumStatus status,
            @RequestParam(required = false) String reason) {

        albumAdminService.updateAlbumStatus(id, status, reason);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Trạng thái album đã được cập nhật thành công")
                .build());
    }
}


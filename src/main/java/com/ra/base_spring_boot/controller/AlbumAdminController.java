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

public class AlbumAdminController {

    private final IAlbumAdminService albumAdminService;

    @GetMapping
    @Operation(summary = "get all album")
    public ResponseEntity<ResponseWrapper<PageResponse<AlbumAdminResponse>>> getAllAlbums(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AlbumStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort (title, releaseDate, createdAt, status)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sorting direction (asc, desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<AlbumAdminResponse> result = albumAdminService.getAllAlbums(
                keyword, status, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ResponseWrapper.<PageResponse<AlbumAdminResponse>>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(result)
                .build());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get album details")
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
    @Operation(summary = "Delete album")
    public ResponseEntity<ResponseWrapper<String>> deleteAlbum(
            @Valid @RequestBody AlbumDeleteRequest request) {

        albumAdminService.deleteAlbum(request);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("The album has been successfully deleted and a notification email has been sent to the artist.")
                .build());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "update status album")
    public ResponseEntity<ResponseWrapper<String>> updateAlbumStatus(
            @PathVariable Long id,
            @RequestParam AlbumStatus status,
            @RequestParam(required = false) String reason) {

        albumAdminService.updateAlbumStatus(id, status, reason);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("The album status has been updated successfully.")
                .build());
    }
}


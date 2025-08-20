package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {
    private final IWishlistService wishlistService;

    @PostMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<?>> addSongToWishlist(@PathVariable Long songId,
                                                                Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(wishlistService.addSongToWishlist(songId, authentication))
                        .build()
        );
    }

    @GetMapping
    public  ResponseEntity<?> getWishlist(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "desc") String sortDir,
                                          Authentication authentication) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(wishlistService.getWishlist(page, size, sortBy, sortDir, authentication))
                        .build()
        );

    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<?>> removeFromWishlist(@PathVariable Long songId,
                                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(wishlistService.removeFromWishlist(songId, authentication))
                        .build()
        );
    }
}

package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {
    private final IWishlistService wishlistService;

    @PostMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<?>> addSongToWishlist(@PathVariable Long songId,
                                                                @AuthenticationPrincipal MyUserDetails currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(wishlistService.addSongToWishlist(songId, currentUser))
                        .build()
        );
    }

    @GetMapping
    public  ResponseEntity<?> getWishlist(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "desc") String sortDir,
                                          @AuthenticationPrincipal MyUserDetails currentUser) {

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(wishlistService.getWishlist(page, size, sortBy, sortDir, currentUser))
                        .build()
        );

    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<?>> removeFromWishlist(@PathVariable Long songId,
                                                                 @AuthenticationPrincipal MyUserDetails currentUser,
                                                                 @RequestParam(defaultValue = "false") boolean confirm) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.NO_CONTENT)
                        .code(HttpStatus.NO_CONTENT.value())
                        .data(wishlistService.removeFromWishlist(songId, currentUser, confirm))
                        .build()
        );
    }
}

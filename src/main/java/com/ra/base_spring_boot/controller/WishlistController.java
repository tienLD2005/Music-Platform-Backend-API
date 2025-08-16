package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.PaginatedResponse;
import com.ra.base_spring_boot.dto.resp.WishlistResponse;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.base.Pagination;
import com.ra.base_spring_boot.services.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ResponseWrapper<Object>> addSongToWishlist(@PathVariable Long songId,
                                                                     @RequestParam Long userId) {
        String message = wishlistService.addSongToWishlist(userId, songId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(message)
                        .build()
        );
    }

    @GetMapping
    public  ResponseEntity<?> getWishlist(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "desc") String sortDir,
                                          Authentication authentication) {

        Page<WishlistResponse> wishlists = wishlistService.getWishlist(page - 1, size, sortBy, sortDir, authentication);
        PaginatedResponse<WishlistResponse> paginated = new PaginatedResponse<>();
        paginated.setItems(wishlists.getContent());
        paginated.setPagination(new Pagination(
                wishlists.getNumber() + 1,
                wishlists.getSize(),
                wishlists.getTotalPages(),
                wishlists.getTotalElements()
        ));
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(paginated)
                        .build()
        );

    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<ResponseWrapper<?>> removeFromWishlist(@PathVariable Long songId,
                                                                 Authentication authentication) {
        String message = wishlistService.removeFromWishlist(songId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(message)
                        .build()
        );
    }
}

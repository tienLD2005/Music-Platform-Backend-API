package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.WishlistResponse;
import org.springframework.security.core.Authentication;

public interface IWishlistService {
    String addSongToWishlist(Long songId, Authentication authentication);

    PageResponse<WishlistResponse> getWishlist(int page, int size, String sortBy, String sortDir, Authentication authentication);

    String removeFromWishlist(Long songId, Authentication authentication);
}

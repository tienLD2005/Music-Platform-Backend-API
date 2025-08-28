package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.WishlistResponse;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import org.springframework.security.core.Authentication;

public interface IWishlistService {
    String addSongToWishlist(Long songId, MyUserDetails currentUser);

    PageResponse<WishlistResponse> getWishlist(int page, int size, String sortBy, String sortDir, MyUserDetails currentUser);

    String removeFromWishlist(Long songId, MyUserDetails currentUser, boolean confirm);
}

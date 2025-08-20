package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.UserListItemResponse;

public interface IUserService {
    PageResponse<UserListItemResponse> getAllUsers(String search, int page, int size);
    void changeUserStatus(Long userId, Boolean block);
}

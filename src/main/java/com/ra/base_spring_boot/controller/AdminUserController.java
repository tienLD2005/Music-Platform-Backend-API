package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.UserListItemResponse;
import com.ra.base_spring_boot.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<PageResponse<UserListItemResponse>>> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<UserListItemResponse> response = userService.getAllUsers(search, page, size);
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<UserListItemResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ResponseWrapper<String>> changeUserStatus(
            @PathVariable Long userId,
            @RequestParam Boolean block
    ) {
        userService.changeUserStatus(userId, block);
        String message = block
                ? "User account has been blocked successfully"
                : "User account has been unblocked successfully";

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(message)
                        .build()
        );
    }

}

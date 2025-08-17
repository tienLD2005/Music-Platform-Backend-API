package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.UserListItemResponse;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IUserRepository userRepository;

    @Override
    public PageResponse<UserListItemResponse> getAllUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("lastName").ascending().and(Sort.by("firstName").ascending())
        );

        Page<User> userPage;

        if (search == null || search.isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, search, pageable
            );
        }

        List<UserListItemResponse> content = userPage.stream()
                .map(UserListItemResponse::fromEntity)
                .toList();

        return PageResponse.<UserListItemResponse>builder()
                .content(content)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(userPage.getSize())
                .build();
    }



    @Override
    public void changeUserStatus(Long userId, Boolean block) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == RoleName.ROLE_ADMIN);

        if (isAdmin && block) {
            throw new RuntimeException("Cannot block an admin account");
        }

        user.setStatus(block ? UStatus.BLOCKED : UStatus.ACTIVE);
        userRepository.save(user);
    }

}

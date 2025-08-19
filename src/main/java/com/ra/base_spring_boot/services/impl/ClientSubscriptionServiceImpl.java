package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.constants.Status;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ra.base_spring_boot.services.IClientSubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientSubscriptionServiceImpl implements IClientSubscriptionService {

    private final ISubscriptionRepository subscriptionRepository;

    @Override
    public SubscriptionResponseDTO getCurrentSubscription() {
        Long userId = SecurityUtil.getCurrentUserId();
        Subscription subscription = subscriptionRepository
                .findByUserIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("No active subscription found"));

        return mapToResponseDTO(subscription);
    }

    @Override
    public List<SubscriptionResponseDTO> getSubscriptionHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        return subscriptionRepository.findAllByUserIdOrderByStartTimeDesc(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO cancelSubscription(Long subscriptionId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this subscription");
        }

        subscription.setStatus(Status.CANCELLED);
        subscription.setEndTime(LocalDateTime.now());
        subscriptionRepository.save(subscription);

        return mapToResponseDTO(subscription);
    }

    private SubscriptionResponseDTO mapToResponseDTO(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .planName(subscription.getPlan_id().getPlanName())
                .price(subscription.getPlan_id().getPrice())
                .startTime(subscription.getStartTime())
                .endTime(subscription.getEndTime())
                .status(subscription.getStatus().name())
                .build();
    }
}

package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.constants.Status;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IClientSubscriptionService;
import com.ra.base_spring_boot.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientSubscriptionServiceImpl implements IClientSubscriptionService {

    private final ISubscriptionRepository subscriptionRepository;
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IUserRepository userRepository;
    private final IPaymentRepository paymentRepository;

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
    public SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (subscriptionRepository.existsByUserIdAndStatus(userId, Status.ACTIVE)) {
            throw new RuntimeException("You already have an active subscription");
        }

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan_id(plan)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(plan.getDurationDay()))
                .status(Status.ACTIVE)
                .build();
        subscriptionRepository.save(subscription);

        Payment payment = Payment.builder()
                .user(user)
                .subscriptionPlan(plan)
                .amount(plan.getPrice())
                .paymentMethod(requestDTO.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            Subscription subToActivate = subscriptionRepository
                    .findFirstByUserIdAndPlanIdAndStatus(userId, plan.getId(), Status.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Pending subscription not found"));

            subToActivate.setStatus(Status.ACTIVE);
            subscriptionRepository.save(subToActivate);
        } else {
            subscription.setStatus(Status.CANCELLED);
            subscriptionRepository.save(subscription);
            throw new RuntimeException("Payment failed, subscription cancelled");
        }

        return mapToResponseDTO(subscription);
    }

    @Override
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

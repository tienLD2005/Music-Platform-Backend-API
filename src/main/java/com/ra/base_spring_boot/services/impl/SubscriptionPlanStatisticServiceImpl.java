package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanStatisticDTO;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.constants.Status;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.services.ISubscriptionPlanStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanStatisticServiceImpl implements ISubscriptionPlanStatisticService {

    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final ISubscriptionRepository subscriptionRepository;
    private final IPaymentRepository paymentRepository;

    @Override
    public PageResponse<SubscriptionPlanStatisticDTO> getStatistics(int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        Page<SubscriptionPlan> planPage = subscriptionPlanRepository.findAll(pageable);

        List<SubscriptionPlanStatisticDTO> dtoList = planPage.stream().map(plan -> {
            List<Subscription> subscriptions = subscriptionRepository.findByPlanIdId(plan.getId());

            Long totalSubscriptions = (long) subscriptions.size();
            Set<Long> userIds = subscriptions.stream()
                    .map(s -> s.getUser().getId())
                    .collect(Collectors.toSet());
            Long totalUsers = (long) userIds.size();

            List<Payment> payments = paymentRepository.findBySubscriptionPlan_IdAndPaymentStatus(
                    plan.getId(), PaymentStatus.COMPLETED
            );
            Double totalRevenue = payments.stream()
                    .mapToDouble(Payment::getAmount)
                    .sum();

            Long cancelledSubscriptions = subscriptions.stream()
                    .filter(s -> s.getStatus() == Status.CANCELLED)
                    .count();

            return new SubscriptionPlanStatisticDTO(
                    plan.getId(),
                    plan.getPlanName(),
                    totalSubscriptions,
                    totalUsers,
                    totalRevenue,
                    cancelledSubscriptions
            );
        }).toList();

        return PageResponse.<SubscriptionPlanStatisticDTO>builder()
                .content(dtoList)
                .currentPage(planPage.getNumber() + 1)
                .totalPages(planPage.getTotalPages())
                .totalElements(planPage.getTotalElements())
                .size(planPage.getSize())
                .build();
    }
}

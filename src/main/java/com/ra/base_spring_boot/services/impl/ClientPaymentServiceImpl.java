package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.mapper.PaymentMapper;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.model.constants.Status;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IClientPaymentService;
import com.ra.base_spring_boot.services.IPaymentProcessor;
import com.ra.base_spring_boot.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientPaymentServiceImpl implements IClientPaymentService{

    private final IPaymentRepository paymentRepository;
    private final ISubscriptionRepository subscriptionRepository;
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IUserRepository userRepository;

    private final List<IPaymentProcessor> processors;

    @Override
    public PaymentResponseDTO getPaymentDetail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return PaymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public String createPayment(SubscriptionRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        Subscription activeSub = subscriptionRepository.findByUserIdAndStatus(userId, Status.ACTIVE).orElse(null);
        if (activeSub != null && !activeSub.getPlan_id().getId().equals(plan.getId())) {
            throw new HttpConflict("You already have an active subscription with another plan");
        }

        PaymentMethod method = PaymentMethod.valueOf(requestDTO.getPaymentMethod());

        return processors.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new HttpBadRequest("Unsupported payment method: " + method))
                .createPayment(user, plan);
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO capturePayment(String orderId, Long paymentId, PaymentMethod method) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        IPaymentProcessor processor = processors.stream()
                .filter(p -> p.supports(method))
                .findFirst()
                .orElseThrow(() -> new HttpBadRequest("Unsupported payment method: " + method));

        Map<String, Object> result = processor.capturePayment(orderId, payment);

        if ("COMPLETED".equalsIgnoreCase((String) result.get("status"))) {
            payment.setTransactionId(orderId);
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            return completeSubscription(payment);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            return null;
        }
    }

    private SubscriptionResponseDTO completeSubscription(Payment payment) {
        Subscription existing = subscriptionRepository.findByUserIdAndStatus(payment.getUser().getId(), Status.ACTIVE).orElse(null);

        Subscription subscription;
        if (existing != null && existing.getPlan_id().getId().equals(payment.getSubscriptionPlan().getId())) {
            existing.setEndTime(existing.getEndTime().plusDays(payment.getSubscriptionPlan().getDurationDay()));
            subscription = subscriptionRepository.save(existing);
        } else {
            subscription = Subscription.builder()
                    .user(payment.getUser())
                    .plan_id(payment.getSubscriptionPlan())
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(payment.getSubscriptionPlan().getDurationDay()))
                    .status(Status.ACTIVE)
                    .build();
            subscriptionRepository.save(subscription);
        }

        return PaymentMapper.toSubscriptionResponse(subscription);
    }
}
package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
import com.ra.base_spring_boot.dto.resp.PaymentResponseDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
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
import com.ra.base_spring_boot.services.paypal.PaypalService;
import com.ra.base_spring_boot.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientPaymentServiceImpl implements IClientPaymentService {

    private final IPaymentRepository paymentRepository;
    private final ISubscriptionRepository subscriptionRepository;
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IUserRepository userRepository;

    private final PaypalService paypalService;

    @Override
    public PaymentResponseDTO getPaymentDetail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found"));
        return mapToResponseDTO(payment);
    }

    @Override
    @Transactional
    public String createPayment(SubscriptionRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(requestDTO.getPlanId())
                .orElseThrow(() -> new HttpNotFound("Plan not found"));

        Subscription activeSub = subscriptionRepository.findByUserIdAndStatus(userId, Status.ACTIVE).orElse(null);
        if (activeSub != null && !activeSub.getPlan_id().getId().equals(plan.getId())) {
            throw new HttpConflict("You already have an active subscription with another plan");
        }

        PaymentMethod method = PaymentMethod.valueOf(requestDTO.getPaymentMethod());

        switch (method) {
            case PAYPAL:
                Payment existingPending = paymentRepository
                        .findByUserIdAndSubscriptionPlanIdAndPaymentStatus(userId, plan.getId(), PaymentStatus.PENDING)
                        .orElse(null);

                if (existingPending != null) {
                    String approvalUrl = paypalService.getApprovalUrl(existingPending.getTransactionId());
                    return "You already have a pending payment. Please approve it: " + approvalUrl;
                }

                Map<String, String> orderResponse = paypalService.createOrder(
                        BigDecimal.valueOf(plan.getPrice()), "USD"
                );

                String orderId = orderResponse.get("orderId");
                String approvalUrl = orderResponse.get("approvalUrl");

                Payment payment = Payment.builder()
                        .user(user)
                        .subscriptionPlan(plan)
                        .amount(plan.getPrice())
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .paymentStatus(PaymentStatus.PENDING)
                        .transactionId(orderId)
                        .build();

                paymentRepository.save(payment);
                return approvalUrl;

            case CREDIT_CARD:
            case MOMO:
            case ZALO_PAY:
                throw new UnsupportedOperationException("Working in progress: " + method.name());
            default:
                throw new HttpBadRequest("Unsupported payment method: " + method.name());
        }
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO capturePayment(String orderId, Long paymentId, PaymentMethod method) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpNotFound("Payment not found"));

        Map<String, Object> captureResult;
        switch (method) {
            case PAYPAL -> captureResult = paypalService.captureOrder(orderId);
            default -> throw new HttpBadRequest("Unsupported payment method: " + method.name());
        }

        if ("COMPLETED".equalsIgnoreCase((String) captureResult.get("status"))) {
            payment.setTransactionId(orderId);
            payment.setPaymentStatus(PaymentStatus.COMPLETED);

            Subscription existing = subscriptionRepository
                    .findByUserIdAndStatus(payment.getUser().getId(), Status.ACTIVE)
                    .orElse(null);

            Subscription subscription;
            if (existing != null) {
                if (existing.getPlan_id().getId().equals(payment.getSubscriptionPlan().getId())) {
                    existing.setEndTime(existing.getEndTime()
                            .plusDays(payment.getSubscriptionPlan().getDurationDay()));
                    subscription = subscriptionRepository.save(existing);
                } else {
                    throw new HttpConflict("You already have an active subscription with another plan");
                }
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

            return SubscriptionResponseDTO.builder()
                    .id(subscription.getId())
                    .planName(subscription.getPlan_id().getPlanName())
                    .price(subscription.getPlan_id().getPrice())
                    .startTime(subscription.getStartTime())
                    .endTime(subscription.getEndTime())
                    .status(subscription.getStatus().name())
                    .build();
        } else {
            payment.setTransactionId(orderId);
            payment.setPaymentStatus(PaymentStatus.FAILED);
            return null;
        }
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO processPaypalSuccess(String orderId, String payerId) {
        Payment payment = paymentRepository.findByTransactionId(orderId)
                .orElseThrow(() -> new HttpNotFound("Payment not found"));

        try {
            Map<String, Object> captureResponse = paypalService.captureOrder(orderId);

            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            SubscriptionPlan plan = payment.getSubscriptionPlan();
            Subscription subscription = Subscription.builder()
                    .user(payment.getUser())
                    .plan_id(plan)
                    .status(Status.ACTIVE)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusDays(plan.getDurationDay()))
                    .build();

            subscriptionRepository.save(subscription);

            return SubscriptionResponseDTO.builder()
                    .id(subscription.getId())
                    .planName(plan.getPlanName())
                    .price(plan.getPrice())
                    .startTime(subscription.getStartTime())
                    .endTime(subscription.getEndTime())
                    .status(subscription.getStatus().name())
                    .build();

        } catch (Exception e) {
            if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
                paymentRepository.delete(payment);
            }
            throw new RuntimeException("Capture PayPal failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processPaypalCancel(String token) {
        try {
            Payment payment = findPaymentByToken(token);
            payment.setTransactionId(token);
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        } catch (HttpNotFound e) {
            log.warn("No pending payment found for token: {}", token);
        }
    }

    @Override
    public Long getPaymentIdByToken(String token) {
        Payment payment = findPaymentByToken(token);
        return payment.getId();
    }

    private Payment findPaymentByToken(String token) {
        Long userId = SecurityUtil.getCurrentUserId();
        return paymentRepository.findPendingPaymentByUser(userId, PaymentStatus.PENDING)
                .orElseThrow(() -> new HttpNotFound("No pending payment found"));
    }

    private SubscriptionResponseDTO completePaymentAndCreateSubscription(
            Payment payment, String token, Map<String, Object> captureResponse) {

        payment.setTransactionId(token);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);
        Subscription subscription = createSubscription(payment);

        return mapToSubscriptionResponseDTO(subscription);
    }

    private void handleFailedPayment(Payment payment, String token, Map<String, Object> captureResponse) {
        payment.setTransactionId(token);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    private Subscription createSubscription(Payment payment) {
        if (subscriptionRepository.existsByUserIdAndStatus(payment.getUser().getId(), Status.ACTIVE)) {
            throw new HttpConflict("User already has an active subscription");
        }

        Subscription subscription = Subscription.builder()
                .user(payment.getUser())
                .plan_id(payment.getSubscriptionPlan())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(payment.getSubscriptionPlan().getDurationDay()))
                .status(Status.ACTIVE)
                .build();

        return subscriptionRepository.save(subscription);
    }

    private SubscriptionResponseDTO mapToSubscriptionResponseDTO(Subscription subscription) {
        return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .planName(subscription.getPlan_id().getPlanName())
                .price(subscription.getPlan_id().getPrice())
                .startTime(subscription.getStartTime())
                .endTime(subscription.getEndTime())
                .status(subscription.getStatus().name())
                .build();
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}

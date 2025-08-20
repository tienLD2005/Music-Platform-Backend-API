package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, Long>{
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findAllByUserIdOrderByPaymentDateDesc(Long userId);
    List<Payment> findAllByPaymentStatus(PaymentStatus status);

    Optional<Payment> findFirstBySubscriptionPlanAndUser(SubscriptionPlan planId, User user);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.paymentStatus = :status")
    Optional<Payment> findPendingPaymentByUser(Long userId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.subscriptionPlan.id = :planId AND p.paymentStatus = :status")
    List<Payment> findBySubscriptionPlan_IdAndPaymentStatus(Long planId, PaymentStatus status);
}

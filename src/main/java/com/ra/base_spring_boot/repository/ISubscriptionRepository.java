package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Subscription;
import com.ra.base_spring_boot.model.constants.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISubscriptionRepository extends JpaRepository<Subscription, Long>{
    boolean existsByUserIdAndStatus(Long userId, Status status);
    List<Subscription> findAllByUserIdOrderByStartTimeDesc(Long userId);
    List<Subscription> findAllByEndTimeBefore(LocalDateTime now);

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.plan_id.id = :planId AND s.status = :status")
    Optional<Subscription> findFirstByUserIdAndPlanIdAndStatus(
            @Param("userId") Long userId,
            @Param("planId") Long planId,
            @Param("status") Status status);

    Optional<Subscription> findByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.plan_id.id = :planId")
    boolean existsByPlanId(Long planId);

    @Query("SELECT s FROM Subscription s WHERE s.plan_id.id = :planId")
    List<Subscription> findByPlanIdId(@Param("planId") Long planId);
}

package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ISubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long>{
    Optional<SubscriptionPlan> findByPlanName(String planName);

    boolean existsByPlanName(String planName);
    Page<SubscriptionPlan> findAllByPlanNameContainingIgnoreCase(String keyword, Pageable pageable);

}

package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long>{
    Optional<SubscriptionPlan> findByPlanName(String planName);
}

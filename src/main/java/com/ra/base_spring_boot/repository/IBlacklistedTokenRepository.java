package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
}


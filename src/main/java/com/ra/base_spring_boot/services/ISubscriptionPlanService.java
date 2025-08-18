package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.SubscriptionPlanRequestDTO;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanResponseDTO;
import org.springframework.data.domain.Page;

public interface ISubscriptionPlanService {
    Page<SubscriptionPlanResponseDTO> getAll(String keyword, int page, int size, String sortBy, String sortDir);
    SubscriptionPlanResponseDTO save(SubscriptionPlanRequestDTO request);
    String delete(Long planId);
}

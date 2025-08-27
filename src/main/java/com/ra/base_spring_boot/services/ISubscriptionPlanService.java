package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.SubscriptionPlanRequestDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanResponseDTO;

public interface ISubscriptionPlanService {
    PageResponse<SubscriptionPlanResponseDTO> getAll(String keyword, int page, int size, String sortBy, String sortDir);
    SubscriptionPlanResponseDTO save(SubscriptionPlanRequestDTO request);
    SubscriptionPlanResponseDTO update(Long planId, SubscriptionPlanRequestDTO request);
    String delete(Long planId, boolean confirm);
}

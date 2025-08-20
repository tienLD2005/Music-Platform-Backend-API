package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanStatisticDTO;

public interface ISubscriptionPlanStatisticService {
    PageResponse<SubscriptionPlanStatisticDTO> getStatistics(int page, int size);
}

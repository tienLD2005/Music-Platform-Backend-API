    package com.ra.base_spring_boot.services;

    import com.ra.base_spring_boot.dto.req.SubscriptionRequestDTO;
    import com.ra.base_spring_boot.dto.resp.SubscriptionResponseDTO;

    import java.util.List;

    public interface IClientSubscriptionService{
        SubscriptionResponseDTO getCurrentSubscription();
        List<SubscriptionResponseDTO> getSubscriptionHistory();
        SubscriptionResponseDTO createSubscription(SubscriptionRequestDTO requestDTO);
        SubscriptionResponseDTO cancelSubscription(Long subscriptionId);
    }

package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.SubscriptionPlanRequestDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SubscriptionPlanResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.ISubscriptionRepository;
import com.ra.base_spring_boot.services.ISubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements ISubscriptionPlanService {
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final ISubscriptionRepository subscriptionRepository;

    @Override
    public PageResponse<SubscriptionPlanResponseDTO> getAll(String keyword, int page, int size, String sortBy, String sortDir) {
        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SubscriptionPlan> planPage;
        if (keyword == null || keyword.isBlank()) {
            planPage = subscriptionPlanRepository.findAll(pageable);
        } else {
            planPage = subscriptionPlanRepository.findAllByPlanNameContainingIgnoreCase(keyword, pageable);
        }

        Page<SubscriptionPlanResponseDTO> plans = planPage.map(subPlan -> new SubscriptionPlanResponseDTO(
                subPlan.getId(),
                subPlan.getPlanName(),
                subPlan.getPrice(),
                subPlan.getDurationDay(),
                subPlan.getDescription()
        ));

        int totalPages = plans.getTotalPages();

        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
        }

        return PageResponse.<SubscriptionPlanResponseDTO>builder()
                .content(plans.getContent())
                .currentPage(plans.getNumber())
                .totalPages(plans.getTotalPages())
                .totalElements(plans.getTotalElements())
                .size(plans.getSize())
                .build();
    }

    @Override
    public SubscriptionPlanResponseDTO save(SubscriptionPlanRequestDTO request) {

        String normalizedPlanName = request.getPlanName().trim().replaceAll("\\s+", " ");

        if (subscriptionPlanRepository.existsByPlanName(normalizedPlanName)) {
            throw new HttpConflict("PlanName already exists");
        }

        SubscriptionPlan subscriptionPlan = SubscriptionPlan.builder()
                .planName(normalizedPlanName)
                .price(request.getPrice())
                .durationDay(request.getDurationDay())
                .description(request.getDescription())
                .build();

        SubscriptionPlan savePlan = subscriptionPlanRepository.save(subscriptionPlan);

        SubscriptionPlanResponseDTO response = new SubscriptionPlanResponseDTO();
        response.setId(savePlan.getId());
        response.setPlanName(savePlan.getPlanName());
        response.setPrice(savePlan.getPrice());
        response.setDurationDay(savePlan.getDurationDay());
        response.setDescription(savePlan.getDescription());

        return response;
    }

    @Override
    public SubscriptionPlanResponseDTO update(Long planId, SubscriptionPlanRequestDTO request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId).orElseThrow(() -> new HttpNotFound("Plan not found"));

        String normalizedPlanName = request.getPlanName().trim().replaceAll("\\s+", " ");

        SubscriptionPlan existing = subscriptionPlanRepository.findByPlanName(normalizedPlanName).orElse(null);
        if (existing != null && !existing.getId().equals(planId)) {
            throw new HttpConflict("PlanName already exists");
        }

        plan.setPlanName(normalizedPlanName);
        plan.setPrice(request.getPrice());
        plan.setDurationDay(request.getDurationDay());
        plan.setDescription(request.getDescription());

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return SubscriptionPlanResponseDTO.builder()
                .id(updatedPlan.getId())
                .planName(updatedPlan.getPlanName())
                .price(updatedPlan.getPrice())
                .durationDay(updatedPlan.getDurationDay())
                .description(updatedPlan.getDescription())
                .build();
    }

    @Override
    public String delete(Long planId, boolean confirm) {
        if (!confirm) {
            throw new HttpBadRequest("Deletion not confirmed. Pass confirm=true to proceed");
        }

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new HttpNotFound("Plan not found"));

        if (subscriptionRepository.existsByPlanId(planId)) {
            throw new HttpBadRequest("Cannot delete the upgrade package. There is a user already registered!");
        }

        subscriptionPlanRepository.delete(plan);

        return "Successfully removed the upgrade package!";
    }


}

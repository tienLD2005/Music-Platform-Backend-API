package com.ra.base_spring_boot.services.impl;


import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements IArtistService{

    private final IUserRepository userRepository;

    @Override
    public List<TrendingArtistResponseDTO> getTrendingArtists(int limit) {
        if (limit < 1) {
            throw new HttpBadRequest("Limit must be greater than 0");
        }

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, limit);

        return userRepository.findTrendingArtists(sevenDaysAgo, pageable);
    }


    @Override
    public PageResponse<TrendingArtistResponseDTO> getAllArtists(Pageable pageable) {
        Page<TrendingArtistResponseDTO> page = userRepository.findAllArtistsAsDTO(pageable);
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }
}
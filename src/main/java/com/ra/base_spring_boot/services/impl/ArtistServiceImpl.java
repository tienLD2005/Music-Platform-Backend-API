package com.ra.base_spring_boot.services.impl;


import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final IUserRepository userRepository;

    @Override
    public List<TrendingArtistResponseDTO> getTrendingArtists(int limit) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, limit);
        return userRepository.findTrendingArtists(sevenDaysAgo, pageable);
    }

}
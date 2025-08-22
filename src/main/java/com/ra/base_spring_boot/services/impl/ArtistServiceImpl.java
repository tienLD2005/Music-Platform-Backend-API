package com.ra.base_spring_boot.services.impl;


import com.ra.base_spring_boot.dto.resp.TrendingArtistResponseDTO;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final IUserRepository userRepository;

    @Override
    public List<TrendingArtistResponseDTO> getTrendingArtists(int limit) {
        return userRepository.findTrendingArtists()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

}
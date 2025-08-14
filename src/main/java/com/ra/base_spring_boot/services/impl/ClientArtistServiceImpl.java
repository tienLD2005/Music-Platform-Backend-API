package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.ArtistResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IClientArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientArtistServiceImpl implements IClientArtistService {

    private final IUserRepository userRepository;

    @Override
    public PageResponse<ArtistResponseDTO> getTrendingArtists(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> artistPage = userRepository.findTrendingArtists(pageable);

        List<ArtistResponseDTO> artistDTOs = artistPage
                .getContent()
                .stream()
                .map(ArtistResponseDTO::fromEntity)
                .toList();

        return PageResponse.<ArtistResponseDTO>builder()
                .content(artistDTOs)
                .currentPage(artistPage.getNumber() + 1)
                .size(artistPage.getSize())
                .totalElements(artistPage.getTotalElements())
                .totalPages(artistPage.getTotalPages())
                .build();
    }

    @Override
    public PageResponse<ArtistResponseDTO> getAllArtists(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> artistPage = userRepository.findAllArtists(pageable);

        List<ArtistResponseDTO> artistDTOs = artistPage
                .getContent()
                .stream()
                .map(ArtistResponseDTO::fromEntity)
                .toList();

        return PageResponse.<ArtistResponseDTO>builder()
                .content(artistDTOs)
                .currentPage(artistPage.getNumber() + 1)
                .size(artistPage.getSize())
                .totalPages(artistPage.getTotalPages())
                .totalElements(artistPage.getTotalElements())
                .build();
    }
}

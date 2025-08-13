package com.ra.base_spring_boot.services.impl;
import com.ra.base_spring_boot.dto.resp.AlbumResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.repository.AlbumRepository;
import com.ra.base_spring_boot.services.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    @Override
    public PageResponse<AlbumResponseDTO> getAlbumsByArtist(Long artistId,
                                                            String title,
                                                            int page,
                                                            int size,
                                                            String sortBy,
                                                            String sortDir) {

        // Tạo đối tượng Sort
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Lấy danh sách album và map sang DTO
        Page<AlbumResponseDTO> albumPage = albumRepository
                .findByArtistAndTitle(artistId, title, pageable)
                .map(album -> AlbumResponseDTO.builder()
                        .id(album.getId())
                        .title(album.getTitle())
                        .releaseDate(album.getReleaseDate())
                        .coverImage(album.getCoverImage())
                        .type(album.getType())
                        .songCount(albumRepository.countSongsInAlbum(album.getId()))
                        .build());

        // Tạo đối tượng PageResponse
        return PageResponse.<AlbumResponseDTO>builder()
                .content(albumPage.getContent())
                .currentPage(albumPage.getNumber())
                .totalPages(albumPage.getTotalPages())
                .totalElements(albumPage.getTotalElements())
                .size(albumPage.getSize())
                .build();
    }
}

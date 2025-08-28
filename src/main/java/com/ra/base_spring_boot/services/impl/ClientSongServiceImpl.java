package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponseDTO;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.SongMapper;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.IClientSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientSongServiceImpl implements IClientSongService{
    private final ISongRepository songRepository;
    private final IGenreRepository genreRepository;
    @Override
    public PageResponse<SongResponseDTO> getSongsByGenre(Long genreId, int page, int size) {
        Page<Song> songPage = songRepository.findByGenreId(genreId, PageRequest.of(page - 1, size));
        if(genreRepository.findById(genreId).isEmpty()) throw new HttpNotFound("Genre not found");
        List<SongResponseDTO> songDTOs = songPage.getContent()
                .stream()
                .map(SongMapper::mapToDTO)
                .toList();
        return PageResponse.<SongResponseDTO>builder()
                .content(songDTOs)
                .currentPage(page)
                .size(size)
                .totalPages(songPage.getTotalPages())
                .totalElements(songPage.getTotalElements())
                .build();
    }
}

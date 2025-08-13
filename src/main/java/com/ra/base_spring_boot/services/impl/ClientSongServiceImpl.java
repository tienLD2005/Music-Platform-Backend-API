package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponseDTO;
import com.ra.base_spring_boot.model.Song;
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

    @Override
    public PageResponse<SongResponseDTO> getSongsByGenre(Long genreId, int page, int size) {
        Page<Song> songPage = songRepository.findByGenreId(genreId, PageRequest.of(page - 1, size));

        List<SongResponseDTO> songDTOs = songPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .toList();

        return PageResponse.<SongResponseDTO>builder()
                .content(songDTOs)
                .currentPage(page)
                .size(size)
                .totalPages(songPage.getTotalPages())
                .totalElements(songPage.getTotalElements())
                .build();
    }

    private SongResponseDTO mapToDTO(Song song) {
        return SongResponseDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .duration(song.getDuration())
                .artistName(song.getArtist() != null ? song.getArtist().getFirstName() + " " + song.getArtist().getLastName() : null)
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .build();
    }
}

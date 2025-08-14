package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponseDTO;
import com.ra.base_spring_boot.services.IClientSongService;
import com.ra.base_spring_boot.services.IClientGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class ClientGenreController{

    private final IClientSongService clientSongService;
    private final IClientGenreService clientGenreService;

    @GetMapping
    public ResponseWrapper<PageResponse<GenreResponseDTO>> getAllGenres(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseWrapper.<PageResponse<GenreResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(clientGenreService.getAllGenres(page, size))
                .build();
    }

    @GetMapping("/{genreId}/view-more")
    public ResponseWrapper<PageResponse<SongResponseDTO>> viewMoreSongs(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<SongResponseDTO> data = clientSongService.getSongsByGenre(genreId, page, size);
        return ResponseWrapper.<PageResponse<SongResponseDTO>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }
}

package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.GenreRequestDTO;
import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.GenreTrendingDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/g-genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingGenres(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<List<GenreTrendingDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(genreService.getTrendingGenres(period, limit))
                        .build()
        );
    }

        //search,sort,show
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getGenres(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<PageResponse<GenreResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(genreService.getGenres(keyword, page, size, sortBy, direction))
                        .build()
        );
    }


    //CRUD
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createGenre(@RequestBody @Valid GenreRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<GenreResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(genreService.createGenre(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateGenre(
            @PathVariable Long id,
            @RequestBody @Valid GenreRequestDTO request
    ) {
        return ResponseEntity.ok(
                ResponseWrapper.<GenreResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(genreService.updateGenre(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Genre deleted successfully")
                        .build()
        );
    }

}
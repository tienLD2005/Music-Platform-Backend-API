package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.GenreRequestDTO;
import com.ra.base_spring_boot.dto.resp.GenreResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/g-genres")
@RequiredArgsConstructor
public class AdminGenreController {

    private final GenreService genreService;

    @GetMapping
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


    @PostMapping
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
package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.PaginatedResponse;
import com.ra.base_spring_boot.dto.resp.ResponseSong;
import com.ra.base_spring_boot.model.base.Pagination;
import com.ra.base_spring_boot.services.IAlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final IAlbumService albumService;

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable Long albumId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "DESC") String direction) {
        Page<ResponseSong> songsPage = albumService.getSongsByAlbum(albumId, page - 1, size, sortBy, direction);

        PaginatedResponse<ResponseSong> paginated = new PaginatedResponse<>();
        paginated.setItems(songsPage.getContent());
        paginated.setPagination(new Pagination(
                songsPage.getNumber() + 1,
                songsPage.getSize(),
                songsPage.getTotalPages(),
                songsPage.getTotalElements()
        ));
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(paginated)
                        .build()
        );
    }

    @PostMapping("/{albumId}/songs")
    public ResponseEntity<?> addSongToAlbum(@PathVariable Long albumId,
                                            @ModelAttribute @Valid FormSong request,
                                            Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(albumService.addSongToAlbum(albumId, request, user.getUsername()))
                        .build()
        );
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> deleteSongFromAlbum(@PathVariable Long albumId,
                                                 @PathVariable Long songId,
                                                 Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        albumService.deleteSongFromAlbum(albumId, songId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(null)
                        .build()
        );
    }

}

package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.resp.AlbumResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.repository.AlbumRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.AlbumService;
import com.ra.base_spring_boot.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final CloudinaryService cloudinaryService;

    private Long getCurrentArtistId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    @Override
    public PageResponse<AlbumResponseDTO> getMyAlbums(String title, int page, int size, String sortBy, String sortDir) {
        Long artistId = getCurrentArtistId();
        return getAlbumsByArtist(artistId, title, page, size, sortBy, sortDir);
    }

    @Override
    public PageResponse<AlbumResponseDTO> getAlbumsByArtist(Long artistId, String title, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AlbumResponseDTO> albumPage = albumRepository.findByArtistAndTitle(artistId, title, pageable)
                .map(album -> AlbumResponseDTO.builder()
                        .id(album.getId())
                        .title(album.getTitle())
                        .releaseDate(album.getReleaseDate())
                        .coverImage(album.getCoverImage())
                        .type(album.getType())
                        .songCount(albumRepository.countSongsInAlbum(album.getId()))
                        .build());

        return PageResponse.<AlbumResponseDTO>builder()
                .content(albumPage.getContent())
                .currentPage(albumPage.getNumber())
                .totalPages(albumPage.getTotalPages())
                .totalElements(albumPage.getTotalElements())
                .size(albumPage.getSize())
                .build();
    }

    @Override
    public ResponseWrapper<AlbumResponseDTO> createAlbum(AlbumRequest request) {
        Long artistId = getCurrentArtistId();

        // Check duplicate title
        if (albumRepository.existsByTitleIgnoreCaseAndArtistId(request.getTitle(), artistId)) {
            return ResponseWrapper.<AlbumResponseDTO>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data(null)
                    .build();
        }

        String coverUrl = null;
        MultipartFile file = request.getCoverImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                coverUrl = cloudinaryService.uploadImage(file);
            } catch (IOException e) {
                return ResponseWrapper.<AlbumResponseDTO>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .data(null)
                        .build();
            }
        }

        Album album = Album.builder()
                .title(request.getTitle())
                .releaseDate(request.getReleaseDate())
                .type(request.getType())
                .coverImage(coverUrl)
                .status(AlbumStatus.PENDING)
                .artist(new User() {{ setId(artistId); }})
                .build();

        Album saved = albumRepository.save(album);

        AlbumResponseDTO dto = AlbumResponseDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .releaseDate(saved.getReleaseDate())
                .coverImage(saved.getCoverImage())
                .type(saved.getType())
                .songCount(0L)
                .build();

        return ResponseWrapper.<AlbumResponseDTO>builder()
                .status(HttpStatus.CREATED)
                .code(HttpStatus.CREATED.value())
                .data(dto)
                .build();
    }

    @Override
    public ResponseWrapper<AlbumResponseDTO> updateAlbum(Long albumId, AlbumRequest request) {
        Long artistId = getCurrentArtistId();

        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) {
            return ResponseWrapper.<AlbumResponseDTO>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .code(HttpStatus.NOT_FOUND.value())
                    .data(null)
                    .build();
        }

        if (!album.getArtist().getId().equals(artistId)) {
            return ResponseWrapper.<AlbumResponseDTO>builder()
                    .status(HttpStatus.FORBIDDEN)
                    .code(HttpStatus.FORBIDDEN.value())
                    .data(null)
                    .build();
        }

        // Check duplicate title
        if (!album.getTitle().equalsIgnoreCase(request.getTitle())
                && albumRepository.existsByTitleIgnoreCaseAndArtistId(request.getTitle(), artistId)) {
            return ResponseWrapper.<AlbumResponseDTO>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data(null)
                    .build();
        }

        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setType(request.getType());

        MultipartFile file = request.getCoverImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                String coverUrl = cloudinaryService.uploadImage(file);
                album.setCoverImage(coverUrl);
            } catch (IOException e) {
                return ResponseWrapper.<AlbumResponseDTO>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .data(null)
                        .build();
            }
        }

        Album saved = albumRepository.save(album);

        AlbumResponseDTO dto = AlbumResponseDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .releaseDate(saved.getReleaseDate())
                .coverImage(saved.getCoverImage())
                .type(saved.getType())
                .songCount(albumRepository.countSongsInAlbum(saved.getId()))
                .build();

        return ResponseWrapper.<AlbumResponseDTO>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(dto)
                .build();
    }

    @Override
    public ResponseWrapper<String> deleteAlbum(Long albumId) {
        Long artistId = getCurrentArtistId();

        Album album = albumRepository.findById(albumId).orElse(null);
        if (album == null) {
            return ResponseWrapper.<String>builder()
                    .status(HttpStatus.NOT_FOUND)
                    .code(HttpStatus.NOT_FOUND.value())
                    .data("Album not found")
                    .build();
        }

        if (!album.getArtist().getId().equals(artistId)) {
            return ResponseWrapper.<String>builder()
                    .status(HttpStatus.FORBIDDEN)
                    .code(HttpStatus.FORBIDDEN.value())
                    .data("You can only delete your own album")
                    .build();
        }

        Long songCount = albumRepository.countSongsInAlbum(albumId);
        if (songCount > 0) {
            return ResponseWrapper.<String>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .data("Album contains songs and cannot be deleted")
                    .build();
        }

        albumRepository.delete(album);
        return ResponseWrapper.<String>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data("Album deleted successfully")
                .build();
    }
}

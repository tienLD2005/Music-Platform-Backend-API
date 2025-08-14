package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpForbiden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.repository.IAlbumRepository;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.cloudinary.CloudinaryService;
import com.ra.base_spring_boot.services.IAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {
    private  final ISongRepository songRepository;
    private final IAlbumRepository albumRepository;
    private final IGenreRepository genreRepository;
    private final IUserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<ResponseSong> getSongsByAlbum(Long albumId, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Song> songsPage = songRepository.findByAlbumId(albumId, pageable);

        List<ResponseSong> dtoList = songsPage.stream()
                .map(song -> new ResponseSong(song.getTitle(), song.getDuration(), song.getViews()))
                .collect(Collectors.toList());

        return  new PageImpl<>(dtoList, pageable, songsPage.getTotalElements());
    }

    @Override
    public ResponseSong addSongToAlbum(Long albumId, FormSong request, String username) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        User artist = userRepository.findById(album.getArtist().getId())
                .orElseThrow(() -> new HttpNotFound("Artist not found"));

        Set<Genre> genres = new HashSet<>();
        if (request.getGenreIds() != null) {
            for (Long genreId : request.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new HttpNotFound("Genre not found"));
                genres.add(genre);
            }
        }

        String fileUrl = cloudinaryService.uploadAudio(request.getFileUrl());

        Song song = Song.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .fileUrl(fileUrl)
                .album(album)
                .genres(genres)
                .artist(artist)
                .views(request.getViews())
                .build();
        songRepository.save(song);
        return new ResponseSong(song.getTitle(), song.getDuration(), song.getViews());
    }

    @Override
    public void deleteSongFromAlbum(Long albumId, Long songId, String name) {
        Song song = songRepository.findById(songId)
                .orElseThrow(()-> new HttpNotFound("Song not found"));

        if (!song.getArtist().getEmail().equals(name)) {
            throw new HttpForbiden("Bạn ko có quyền xóa bài hát này");
        }

        if (!song.getAlbum().getId().equals(albumId)) {
            throw new HttpBadRequest("Bài hát ko thuộc album này");
        }

        songRepository.delete(song);
    }

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

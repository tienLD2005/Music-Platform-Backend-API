package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.ResponseAlbum;
import com.ra.base_spring_boot.dto.resp.ResponseGenre;
import com.ra.base_spring_boot.dto.resp.ResponseSong;
import com.ra.base_spring_boot.dto.resp.ResponseUser;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.IAlbumRepository;
import com.ra.base_spring_boot.repository.IGenreRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.CloudinaryService;
import com.ra.base_spring_boot.services.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private  final ISongRepository songRepository;
    private final IAlbumRepository albumRepository;
    private final IGenreRepository genreRepository;
    private final IUserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<ResponseSong> getSongsByAlbum(Long albumId, int page, int size) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));
        
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách bài hát theo album
        Page<Song> songsPage = songRepository.findByAlbumId(album.getId(), pageable);

        return songsPage.map(this::convertToResponseSong);
    }

    @Override
    public ResponseSong addSongToAlbum(FormSong request) {
        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        User artist = userRepository.findById(album.getArtist().getId())
                .orElseThrow(() -> new HttpNotFound("Artist not found"));

        Set<Genre> genres = new HashSet<>();
        if (request.getGenreIds() != null) {
            genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
        }

        // Đường dẫn file upload lên Cloudinary
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
        // Lưu bài hát vào cơ sở dữ liệu
        Song savedSong = songRepository.save(song);

        return convertToResponseSong(savedSong);
    }

    // Hàm chuyển đổi từ Song sang ResponseSong
    private ResponseSong convertToResponseSong(Song song) {
        ResponseUser responseUser = ResponseUser.builder()
                .id(song.getArtist().getId())
                .firstName(song.getArtist().getFirstName())
                .lastName(song.getArtist().getLastName())
                .email(song.getArtist().getEmail())
                .profileImage(song.getArtist().getProfileImage())
                .build();

        ResponseAlbum responseAlbum = ResponseAlbum.builder()
                .id(song.getAlbum().getId())
                .title(song.getAlbum().getTitle())
                .coverImage(song.getAlbum().getCoverImage())
                .type(song.getAlbum().getType())
                .build();
        return ResponseSong.builder()
                .id(song.getId())
                .title(song.getTitle())
                .duration(song.getDuration())
                .album(responseAlbum)
                .artist(responseUser)
                .views(song.getViews())
                .fileUrl(song.getFileUrl())
                .createdAt(song.getCreatedAt())
                .updatedAt(song.getUpdatedAt())
                .genres(song.getGenres().stream()
                        .map(genre -> ResponseGenre.builder()
                                .id(genre.getId())
                                .genreName(genre.getGenreName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}

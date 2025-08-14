package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormSong;
import com.ra.base_spring_boot.dto.resp.ResponseAlbum;
import com.ra.base_spring_boot.dto.resp.ResponseGenre;
import com.ra.base_spring_boot.dto.resp.ResponseSong;
import com.ra.base_spring_boot.dto.resp.ResponseUser;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpForbiden;
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
import com.ra.base_spring_boot.services.IAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
        songRepository.save(song);
        return new ResponseSong(song.getTitle(), song.getDuration(), song.getViews());
    }

    @Override
    public void deleteSongFromAlbum(Long albumId, Long songId, String name) {
        Song song = songRepository.findById(songId)
                .orElseThrow(()-> new HttpNotFound("Song not found"));

        // Kiểm tra quyền sở hữu
        if (!song.getArtist().getEmail().equals(name)) {
            throw new HttpForbiden("Bạn ko có quyền xóa bài hát này");
        }

        if (!song.getAlbum().getId().equals(albumId)) {
            throw new HttpBadRequest("Bài hát ko thuộc album này");
        }

        songRepository.delete(song);
    }

}

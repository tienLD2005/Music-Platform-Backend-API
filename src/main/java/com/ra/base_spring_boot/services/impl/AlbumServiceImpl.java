package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.req.FormSongRequest;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.model.constants.SongStatus;
import com.ra.base_spring_boot.repository.*;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {
    private  final ISongRepository songRepository;
    private final IAlbumRepository albumRepository;
    private final IGenreRepository genreRepository;
    private final IUserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public PageResponse<ResponseSong> getSongsByAlbum(Long albumId, int page, int size, String sortBy, String direction) {
        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));


        Page<Song> songsPage = songRepository.findByAlbumIdAndStatus(album.getId(), pageable, SongStatus.APPROVED);

        Page<ResponseSong> songs = songsPage.map(song -> ResponseSong.builder()
                .id(song.getId())
                .artistName(song.getArtist() != null
                        ? song.getArtist().getFirstName() + " " + song.getArtist().getLastName()
                        : null)
                .title(song.getTitle())
                .duration(song.getDuration())
                .views(song.getViews())
                .fileUrl(song.getFileUrl())
                .build());

        int totalPages = songs.getTotalPages();

        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
        }

        return PageResponse.<ResponseSong>builder()
                .content(songs.getContent())
                .currentPage(songs.getNumber())
                .totalPages(songs.getTotalPages())
                .totalElements(songs.getTotalElements())
                .size(songs.getSize())
                .build();
    }

    @Override
    public ResponseSong addSongToAlbum(Long albumId, FormSongRequest request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        User artist = userRepository.findById(album.getArtist().getId())
                .orElseThrow(() -> new HttpNotFound("Artist not found"));

        if(album.getStatus()!=AlbumStatus.ACTIVE){
            throw new HttpForbidden("Album status is not ACTIVE");

        }

        // Check duplicate title song
        if (songRepository.existsByTitleAndAlbumId(request.getTitle(), albumId)) {
            throw new HttpBadRequest("Song with this title already exists in the album");
        }

        if(request.getFileUrl() ==  null) {
            throw new HttpBadRequest("File url is required");
        }

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
                .title(request.getTitle().trim().replaceAll("\\s+", " "))
                .duration(request.getDuration())
                .fileUrl(fileUrl)
                .album(album)
                .genres(genres)
                .artist(artist)
                .status(SongStatus.PENDING)
                .views(0)
                .build();
        songRepository.save(song);
        return ResponseSong.builder()
                .id(song.getId())
                .artistName(song.getArtist() != null
                        ? song.getArtist().getFirstName() + " " + song.getArtist().getLastName()
                        : null)
                .title(song.getTitle())
                .views(song.getViews())
                .duration(song.getDuration())
                .fileUrl(song.getFileUrl())
                .build();
    }

    @Override
    public String deleteSongFromAlbum(Long albumId, Long songId, MyUserDetails principal) {
        Song song = songRepository.findById(songId)
                .orElseThrow(()-> new HttpNotFound("Song not found"));

        if (!song.getArtist().getId().equals(principal.getId())) {
            throw new HttpForbidden("You do not have permission to delete this song");
        }

        if (!song.getAlbum().getId().equals(albumId)) {
            throw new HttpBadRequest("This song does not belong to the album");
        }

        song.setStatus(SongStatus.REJECTED);
        songRepository.save(song);
        return "Song delete from album successfully";
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
        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
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

        int totalPages = albumPage.getTotalPages();

        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
        }

        return PageResponse.<AlbumResponseDTO>builder()
                .content(albumPage.getContent())
                .currentPage(albumPage.getNumber())
                .totalPages(totalPages)
                .totalElements(albumPage.getTotalElements())
                .size(albumPage.getSize())
                .build();
    }

    @Override
    public ResponseWrapper<AlbumResponseDTO> createAlbum(AlbumRequest request) {
        Long artistId = getCurrentArtistId();

        if (request.getCoverImageFile() == null) {
            throw new HttpBadRequest("Cover image is required");
        }

        if (request.getReleaseDate().isBefore(LocalDateTime.now())) {
            throw new HttpBadRequest("Release date must be in the present or future");
        }

        // Check duplicate title
        if (albumRepository.existsByTitleIgnoreCaseAndArtistId(request.getTitle(), artistId)) {
            throw new HttpConflict("Album with this title already exists");
        }

        String coverUrl = null;
        MultipartFile file = request.getCoverImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                coverUrl = cloudinaryService.uploadImage(file);
            } catch (RuntimeException e){
                throw new HttpBadRequest("Invalid image file");
            }
        }

        Album album = Album.builder()
                .title(request.getTitle().trim().replaceAll("\\s+", " "))
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

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        if (!album.getArtist().getId().equals(artistId)) {
            throw new HttpForbidden("You do not have permission to update this album");
        }

        if (request.getReleaseDate().isBefore(LocalDateTime.now())) {
            throw new HttpBadRequest("Release date must be in the present or future");
        }

        // Check duplicate title
        if (!album.getTitle().equalsIgnoreCase(request.getTitle())
                && albumRepository.existsByTitleIgnoreCaseAndArtistId(request.getTitle(), artistId)) {
            throw new HttpConflict("Album with this title already exists");
        }

        album.setTitle(request.getTitle().trim().replaceAll("\\s+", " "));
        album.setReleaseDate(request.getReleaseDate());
        album.setType(request.getType());

        MultipartFile file = request.getCoverImageFile();
        if (file != null && !file.isEmpty()) {
            try {
                String coverUrl = cloudinaryService.uploadImage(file);
                album.setCoverImage(coverUrl);
            } catch (RuntimeException e){
                throw new HttpBadRequest("Invalid image file");
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

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

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
                .status(HttpStatus.NO_CONTENT)
                .code(HttpStatus.NO_CONTENT.value())
                .data("Album deleted successfully")
                .build();
    }

    // List Album
    @Override
    public PageResponse<AlbumResponse> getAllAlbums(int page, int size, String sortBy, String sortDir, String keyword) {

        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);


        Page<Album> albumPage;

        if (keyword != null && !keyword.isEmpty()) {
            albumPage = albumRepository.searchActiveAlbums(AlbumStatus.ACTIVE, keyword, pageable);
        }else {
            albumPage = albumRepository.findByStatus(AlbumStatus.ACTIVE, pageable);
        }

        Page<AlbumResponse> albums = albumPage.map(album -> AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImage(album.getCoverImage())
                .releaseDate(album.getReleaseDate())
                .type(album.getType())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : null)
                .songCount(albumRepository.countSongsInAlbum(album.getId()))
                .build());

        int totalPages = albums.getTotalPages();

        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
        }

        return PageResponse.<AlbumResponse>builder()
                .content(albums.getContent())
                .currentPage(albums.getNumber())
                .totalPages(albums.getTotalPages())
                .totalElements(albums.getTotalElements())
                .size(albums.getSize())
                .build();
    }

    @Override
    public PageResponse<AlbumResponse> getTopAlbums(String period) {

        LocalDateTime fromDate = switch (period.toLowerCase()) {
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            default -> LocalDateTime.MIN;
        };

        Pageable pageable = PageRequest.of(0, 15);

        Page<Album> albumPage = albumRepository.findTopAlbumsByViewsSince(AlbumStatus.ACTIVE, fromDate, pageable);

        Page<AlbumResponse> albums = albumPage.map(album -> AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImage(album.getCoverImage())
                .releaseDate(album.getReleaseDate())
                .type(album.getType())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : null)
                .songCount(albumRepository.countSongsInAlbum(album.getId()))
                .build());

        return PageResponse.<AlbumResponse>builder()
                .content(albums.getContent())
                .currentPage(albums.getNumber() + 1)
                .totalPages(albums.getTotalPages())
                .totalElements(albums.getTotalElements())
                .size(albums.getSize())
                .build();
    }

    @Override
    public PageResponse<AlbumResponse> findFeaturedAlbums() {

        Pageable pageable = PageRequest.of(0, 5);
        Page<Album> albumPage = albumRepository.findFeaturedAlbums(AlbumStatus.ACTIVE, pageable);

        Page<AlbumResponse> albums = albumPage.map(album -> AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImage(album.getCoverImage())
                .releaseDate(album.getReleaseDate())
                .type(album.getType())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : null)
                .songCount(albumRepository.countSongsInAlbum(album.getId()))
                .build());

        return PageResponse.<AlbumResponse>builder()
                .content(albums.getContent())
                .currentPage(albums.getNumber() + 1)
                .totalPages(albums.getTotalPages())
                .totalElements(albums.getTotalElements())
                .size(albums.getSize())
                .build();
    }

    @Override
    public PageResponse<AlbumResponse> getAlbumsByArtistWithRoleGuest(Long artistId, int page, int size, String keyword, String sortDir, boolean isPremium) {
        User user = userRepository.findById(artistId).orElseThrow(() -> new HttpNotFound("Artist not found"));

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("releaseDate").ascending() : Sort.by("releaseDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Album> albumPage = albumRepository.findAlbumsByArtist(
                user.getId(),
                keyword,
                AlbumStatus.ACTIVE,
                pageable
        );


        Page<AlbumResponse> albums = albumPage.map(album -> AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .coverImage(album.getCoverImage())
                .releaseDate(album.getReleaseDate())
                .type(album.getType())
                .artistName(album.getArtist() != null
                        ? album.getArtist().getFirstName() + " " + album.getArtist().getLastName()
                        : null)
                .songCount(albumRepository.countSongsInAlbum(album.getId()))
                .streamUrls(album.getSongs().stream().map(Song::getFileUrl).toList())
                .downloadUrl(isPremium ? (album.getSongs()
                        .stream().map(Song::getFileUrl).toList()) : null)
                .build()
        );

        return PageResponse.<AlbumResponse>builder()
                .content(albums.getContent())
                .currentPage(albums.getNumber())
                .totalPages(albums.getTotalPages())
                .totalElements(albums.getTotalElements())
                .size(albums.getSize())
                .build();
    }

    @Override
    public List<AlbumResponse> getTopTrendingAlbums(int page, int limit) {
        if (page < 0) {
            throw new HttpBadRequest("Page index must not be less than zero");
        }
        if (limit <= 0) {
            throw new HttpBadRequest("Page size must be greater than zero");
        }

        Pageable pageable = PageRequest.of(page, limit);

        return albumRepository.findTopTrendingAlbumsWithViews(pageable)
                .stream()
                .map(dto -> AlbumResponse.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .coverImage(dto.getCoverImage())
                        .artistName(dto.getArtistFirstName() != null
                                ? dto.getArtistFirstName() + " " + dto.getArtistLastName()
                                : null)
                        .songCount(dto.getSongCount())
                        .totalPlays(dto.getTotalPlays())
                        .build()
                )
                .toList();
    }


}

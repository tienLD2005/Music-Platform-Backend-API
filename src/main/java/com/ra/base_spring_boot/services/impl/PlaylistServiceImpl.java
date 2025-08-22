package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.base.PlaylistSongId;
import com.ra.base_spring_boot.repository.IPlaylistRepository;
import com.ra.base_spring_boot.repository.IPlaylistSongRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IPlaylistService;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PlaylistServiceImpl implements IPlaylistService {

    private final IPlaylistRepository playlistRepository;
    private final IUserRepository userRepository;
    private final ISongRepository songRepository;
    private final IPlaylistSongRepository playlistSongRepository;

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "name");

    public PlaylistServiceImpl(IPlaylistRepository playlistRepository,
                               IUserRepository userRepository,
                               ISongRepository songRepository,
                               IPlaylistSongRepository playlistSongRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.playlistSongRepository = playlistSongRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpNotFound("Current user not found"));
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    @Override
    public PageResponse<PlaylistResp> searchOfCurrentUser(String q, int page, int size, String sortBy, String direction) {
        User currentUser = getCurrentUser();

        String keyword = (q == null || q.trim().isEmpty()) ? null : q.trim();
        String sortField = ALLOWED_SORTS.contains(sortBy) ? sortBy : "createdAt";

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);

        Page<Playlist> playlists = playlistRepository.findByUserIdAndKeyword(currentUser.getId(), keyword, pageable);

        return PageResponse.<PlaylistResp>builder()
                .content(playlists.getContent().stream()
                        .map(pl -> new PlaylistResp(
                                pl.getId(),
                                pl.getName(),
                                pl.getIsPublic(),
                                pl.getCreatedAt(),
                                pl.getUpdatedAt()
                        )).toList())
                .currentPage(playlists.getNumber())
                .totalPages(playlists.getTotalPages())
                .totalElements(playlists.getTotalElements())
                .size(playlists.getSize())
                .build();
    }

    @Override
    public PlaylistResp createPlaylist(PlaylistReq request) {
        User currentUser = getCurrentUser();

        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setIsPublic(request.getIsPublic());
        playlist.setUser(currentUser);
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setUpdatedAt(LocalDateTime.now());

        playlistRepository.save(playlist);

        return new PlaylistResp(
                playlist.getId(),
                playlist.getName(),
                playlist.getIsPublic(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );
    }

    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        PlaylistSongId id = new PlaylistSongId(playlistId, songId);

        if (playlistSongRepository.existsById(id)) {
            throw new HttpConflict("Song already in playlist");
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setId(id);
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSong.setAddedAt(LocalDateTime.now());

        playlistSongRepository.save(playlistSong);
    }

    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        PlaylistSongId id = new PlaylistSongId(playlistId, songId);

        if (!playlistSongRepository.existsById(id)) {
            throw new HttpNotFound("Song not found in playlist");
        }

        playlistSongRepository.deleteById(id);
    }

    @Override
    public List<SongResponse> getSongsInPlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        return playlist.getPlaylistSongs()
                .stream()
                .map(PlaylistSong::getSong)
                .map(song -> SongResponse.builder()
                        .id(song.getId())
                        .title(song.getTitle())
                        .duration(song.getDuration())
                        .artistName(song.getArtist().getFirstName())
                        .artistId(song.getArtist().getId())
                        .albumName(song.getAlbum().getTitle())
                        .albumId(song.getAlbum().getId())
                        .fileUrl(song.getFileUrl())
                        .views(song.getViews())
                        .createdAt(song.getCreatedAt())
                        .genres(song.getGenres().stream()
                                .map(Genre::getGenreName)
                                .toList())
                        .build()
                )
                .toList();
    }

    @Override
    public PlaylistResp updatePlaylist(Long playlistId, PlaylistReq request) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        playlist.setName(request.getName());
        playlist.setIsPublic(request.getIsPublic());
        playlist.setUpdatedAt(LocalDateTime.now());

        playlistRepository.save(playlist);

        return new PlaylistResp(
                playlist.getId(),
                playlist.getName(),
                playlist.getIsPublic(),
                playlist.getCreatedAt(),
                playlist.getUpdatedAt()
        );
    }

    @Override
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        playlistRepository.delete(playlist);
    }
}

    package com.ra.base_spring_boot.services.impl;

    import com.ra.base_spring_boot.dto.req.PlaylistReq;
    import com.ra.base_spring_boot.dto.resp.PageResponse;
    import com.ra.base_spring_boot.dto.resp.PlaylistResp;
    import com.ra.base_spring_boot.dto.resp.SongResponse;
    import com.ra.base_spring_boot.exception.HttpNotFound;
    import com.ra.base_spring_boot.model.Playlist;
    import com.ra.base_spring_boot.model.PlaylistSong;
    import com.ra.base_spring_boot.model.Song;
    import com.ra.base_spring_boot.model.User;
    import com.ra.base_spring_boot.model.base.PlaylistSongId;
    import com.ra.base_spring_boot.repository.ISongRepository;
    import com.ra.base_spring_boot.repository.IUserRepository;
    import com.ra.base_spring_boot.repository.IPlaylistRepository;
    import com.ra.base_spring_boot.repository.IPlaylistSongRepository;
    import com.ra.base_spring_boot.services.IPlaylistService;
    import org.springframework.data.domain.*;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Set;

    @Service
    public class PlaylistServiceImpl implements IPlaylistService {

        private final IPlaylistRepository IPlaylistRepository;
        private final IUserRepository userRepository;
        private final ISongRepository songRepository;
        private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "name");
        private final IPlaylistSongRepository IPlaylistSongRepository;

        public PlaylistServiceImpl(
                IPlaylistRepository IPlaylistRepository,
                IUserRepository userRepository,
                ISongRepository songRepository,
                IPlaylistSongRepository IPlaylistSongRepository) {
            this.IPlaylistRepository = IPlaylistRepository;
            this.userRepository = userRepository;
            this.songRepository = songRepository;
            this.IPlaylistSongRepository = IPlaylistSongRepository;
        }

        @Override
        public List<Playlist> getAllPlaylists() {
            return IPlaylistRepository.findAll();
        }

        @Override
        public PageResponse<PlaylistResp> searchOfUser(Long userId, String q, int page, int size, String sortBy, String direction) {
            String keyword = (q == null || q.trim().isEmpty()) ? null : q.trim();
            String sortField = ALLOWED_SORTS.contains(sortBy) ? sortBy : "createdAt";

            Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();

            Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);
            Page<Playlist> playlists = IPlaylistRepository.findByUserIdAndKeyword(userId, keyword, pageable);

            return PageResponse.<PlaylistResp>builder()
                    .content(playlists.getContent().stream().map(pl -> new PlaylistResp(
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
        public void addSongToPlaylist(Long playlistId, Long songId) {
            Playlist playlist = IPlaylistRepository.findById(playlistId)
                    .orElseThrow(() -> new HttpNotFound("Playlist not found"));

            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new HttpNotFound("Song not found"));

            PlaylistSongId id = new PlaylistSongId(playlistId, songId);

            // Check if already exists
            if (IPlaylistSongRepository.existsById(id)) {
                throw new HttpNotFound("Song already in playlist");
            }

            PlaylistSong playlistSong = new PlaylistSong();
            playlistSong.setId(id);
            playlistSong.setPlaylist(playlist);
            playlistSong.setSong(song);
            playlistSong.setAddedAt(LocalDateTime.now());

            IPlaylistSongRepository.save(playlistSong);
        }

        @Override
        public void removeSongFromPlaylist(Long playlistId, Long songId) {
            PlaylistSongId id = new PlaylistSongId(playlistId, songId);

            if (!IPlaylistSongRepository.existsById(id)) {
                throw new HttpNotFound("Song not found in playlist");
            }

            IPlaylistSongRepository.deleteById(id);
        }

        @Override
        public PlaylistResp createPlaylist(Long userId, PlaylistReq request) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new HttpNotFound("User not found"));

            Playlist playlist = new Playlist();
            playlist.setName(request.getName());
            playlist.setIsPublic(request.getIsPublic());
            playlist.setUser(user);
            playlist.setCreatedAt(LocalDateTime.now());
            playlist.setUpdatedAt(LocalDateTime.now());

            IPlaylistRepository.save(playlist);

            return new PlaylistResp(
                    playlist.getId(),
                    playlist.getName(),
                    playlist.getIsPublic(),
                    playlist.getCreatedAt(),
                    playlist.getUpdatedAt()
            );
        }


        @Override
        public List<SongResponse> getSongsInPlaylist(Long playlistId) {
            Playlist playlist = IPlaylistRepository.findById(playlistId)
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
//                            .status(song.getStatus())
                            .genres(song.getGenres().stream()
                                    .map(g -> g.getGenreName())
                                    .toList())
                            .build()
                    )
                    .toList();
        }


    }


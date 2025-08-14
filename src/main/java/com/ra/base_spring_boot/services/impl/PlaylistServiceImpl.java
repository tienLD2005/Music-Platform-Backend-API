    package com.ra.base_spring_boot.services.impl;

    import com.ra.base_spring_boot.dto.req.PlaylistReq;
    import com.ra.base_spring_boot.dto.resp.PlaylistResp;
    import com.ra.base_spring_boot.model.Playlist;
    import com.ra.base_spring_boot.model.PlaylistSong;
    import com.ra.base_spring_boot.model.Song;
    import com.ra.base_spring_boot.model.User;
    import com.ra.base_spring_boot.model.base.PlaylistSongId;
    import com.ra.base_spring_boot.repository.ISongRepository;
    import com.ra.base_spring_boot.repository.IUserRepository;
    import com.ra.base_spring_boot.repository.PlaylistRepository;
    import com.ra.base_spring_boot.repository.PlaylistSongRepository;
    import com.ra.base_spring_boot.services.IPlaylistService;
    import org.springframework.data.domain.*;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Set;

    @Service
    public class PlaylistServiceImpl implements IPlaylistService {

        private final PlaylistRepository playlistRepository;
        private final IUserRepository userRepository;
        private final ISongRepository songRepository;
        private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "name");
        private final PlaylistSongRepository playlistSongRepository;

        public PlaylistServiceImpl(
                PlaylistRepository playlistRepository,
                IUserRepository userRepository,
                ISongRepository songRepository,
                PlaylistSongRepository playlistSongRepository) {
            this.playlistRepository = playlistRepository;
            this.userRepository = userRepository;
            this.songRepository = songRepository;
            this.playlistSongRepository = playlistSongRepository;
        }

        @Override
        public List<Playlist> getAllPlaylists() {
            return playlistRepository.findAll();
        }

        @Override
        public Page<PlaylistResp> searchOfUser(Long userId, String q, int page, int size, String sortBy, String direction) {
            String keyword = (q == null || q.trim().isEmpty()) ? null : q.trim();
            String sortField = ALLOWED_SORTS.contains(sortBy) ? sortBy : "createdAt";

            Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();

            Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);
            Page<Playlist> playlists = playlistRepository.findByUserIdAndKeyword(userId, keyword, pageable);

            return playlists.map(pl -> new PlaylistResp(
                    pl.getId(),
                    pl.getName(),
                    pl.getIsPublic(),
                    pl.getCreatedAt(),
                    pl.getUpdatedAt()
            ));
        }

        @Override
        public void addSongToPlaylist(Long playlistId, Long songId) {
            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new RuntimeException("Playlist not found"));

            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Song not found"));

            PlaylistSongId id = new PlaylistSongId(playlistId, songId);

            // Check if already exists
            if (playlistSongRepository.existsById(id)) {
                throw new RuntimeException("Song already in playlist");
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
                throw new RuntimeException("Song not found in playlist");
            }

            playlistSongRepository.deleteById(id);
        }

        @Override
        public PlaylistResp createPlaylist(Long userId, PlaylistReq request) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Playlist playlist = new Playlist();
            playlist.setName(request.getName());
            playlist.setIsPublic(request.getIsPublic());
            playlist.setUser(user);
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


    }


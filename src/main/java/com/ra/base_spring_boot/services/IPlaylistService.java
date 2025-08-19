package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.model.Playlist;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPlaylistService {
    List<Playlist> getAllPlaylists();

    PageResponse<PlaylistResp> searchOfUser(Long userId,
                                            String q,
                                            int page,
                                            int size,
                                            String sortBy,
                                            String direction);

    PlaylistResp createPlaylist(Long userId, PlaylistReq request);
    void addSongToPlaylist(Long playlistId, Long songId);
    void removeSongFromPlaylist(Long playlistId, Long songId);
    List<SongResponse> getSongsInPlaylist(Long playlistId);

}

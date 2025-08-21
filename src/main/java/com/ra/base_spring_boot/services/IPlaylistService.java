package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.model.Playlist;

import java.util.List;

public interface IPlaylistService {

    List<Playlist> getAllPlaylists();

    PageResponse<PlaylistResp> searchOfCurrentUser(String keyword,
                                                   int page,
                                                   int size,
                                                   String sortBy,
                                                   String direction);

    PlaylistResp createPlaylist(PlaylistReq request);

    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

    List<SongResponse> getSongsInPlaylist(Long playlistId);

    PlaylistResp updatePlaylist(Long playlistId, PlaylistReq request);

    void deletePlaylist(Long playlistId);
}

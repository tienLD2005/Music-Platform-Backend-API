package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.model.Playlist;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPlaylistService {
    List<Playlist> getAllPlaylists();

    /**

     *
     * @param userId    id user
     * @param q         name search
     * @param page      page (0-based)
     * @param size      size
     * @param sortBy    sort: "createdAt" , "name"
     * @param direction "asc" , "desc"
     * @return Page<PlaylistResp>
     */
    Page<PlaylistResp> searchOfUser(Long userId,
                                    String q,
                                    int page,
                                    int size,
                                    String sortBy,
                                    String direction);

    PlaylistResp createPlaylist(Long userId, PlaylistReq request);
    void addSongToPlaylist(Long playlistId, Long songId);
    void removeSongFromPlaylist(Long playlistId, Long songId);
}

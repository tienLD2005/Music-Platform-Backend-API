package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.repository.ISongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ISongService {
    List<TopSongOfWeek> getTop15SongsOfWeek();
    List<TopSongDTO> getTopSongsAllTime(int limit);
    List<TrendingSongResponseDTO> getTrendingSongs(LocalDateTime fromTime, int limit);

    PageResponse<SongResponse> getAllSongs(String keyword, Pageable pageable);

    void deleteSong(Long songId, String reason, String adminName);



}

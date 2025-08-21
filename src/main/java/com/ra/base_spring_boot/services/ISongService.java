package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.repository.ISongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISongService {
    List<TopSongDTO> getTop15SongsOfWeek();
    List<TopSongDTO> getTopSongsAllTime(int limit);
    List<TopSongDTO> getTrendingSongs(int limit);

    PageResponse<SongResponse> getAllSongs(String keyword, Pageable pageable);

    void deleteSong(Long songId, String reason, String adminName);



}

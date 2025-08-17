package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private final ISongRepository songRepository;

    @Override
    public List<TopSongDTO> getTop15SongsOfWeek() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable top15 = PageRequest.of(0, 15);
        return songRepository.findTopSongsOfWeek(sevenDaysAgo, top15);
    }

    @Override
    public List<TopSongDTO> getTopSongsAllTime(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTopSongsAllTime(pageable);
    }

    @Override
    public List<TopSongDTO> getTrendingSongs(int limit) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTrendingSongs(threeDaysAgo, pageable);
    }

}

package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.DownloadSongRequest;
import com.ra.base_spring_boot.dto.resp.DownloadResponse;
import com.ra.base_spring_boot.dto.resp.DownloadedSongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDownloadSongService {
    DownloadResponse downloadSong(Long userId, DownloadSongRequest request);
    Page<DownloadedSongResponse> getDownloadedSongs(Long userId, Pageable pageable);
    List<DownloadedSongResponse> getDownloadedSongsSorted(Long userId, String sortBy);
    DownloadResponse removeDownloadedSong(Long userId, Long songId);
    boolean isAlreadyDownloaded(Long userId, Long songId);
    long getDownloadCount(Long userId);
}

package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongHistoryResponse;

public interface ISongHistoryService {
    PageResponse<SongHistoryResponse> getRecentHistory(Long userId, int page, int size);
    void addPlay(Long userId, Long songId);
}

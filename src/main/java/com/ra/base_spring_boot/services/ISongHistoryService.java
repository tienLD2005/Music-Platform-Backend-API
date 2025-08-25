package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongHistoryResponse;
import com.ra.base_spring_boot.model.SongHistory;

public interface ISongHistoryService {
    PageResponse<SongHistoryResponse> getRecentHistory(Long userId, int page, int size);
    SongHistoryResponse addPlay(Long userId, Long songId);
}

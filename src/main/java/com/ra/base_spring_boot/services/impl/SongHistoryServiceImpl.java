package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongHistoryResponse;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.base.SongHistoryId;
import com.ra.base_spring_boot.model.constants.SongStatus;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.services.ISongHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SongHistoryServiceImpl implements ISongHistoryService {

    private final ISongHistoryRepository songHistoryRepository;
    private final IUserRepository userRepository;
    private final ISongRepository songRepository;

    @Override
    public PageResponse<SongHistoryResponse> getRecentHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "playedAt"));
        Page<SongHistoryResponse> p = songHistoryRepository.findRecentView(userId, pageable);

        return PageResponse.<SongHistoryResponse>builder()
                .content(p.getContent())
                .currentPage(p.getNumber())
                .totalPages(p.getTotalPages())
                .totalElements(p.getTotalElements())
                .size(p.getSize())
                .build();
    }


    @Override
    @Transactional
    public void addPlay(Long userId, Long songId) {
        Song song = songRepository.findById(songId).orElseThrow(()-> new HttpNotFound("Song not found"));

        if (song.getStatus() != SongStatus.APPROVED) {
            throw new HttpForbidden("Song status is not APPROVED");
        }

        SongHistoryId id = new SongHistoryId(userId, songId);
        SongHistory history = songHistoryRepository.findById(id).orElseGet(() -> {
            SongHistory sh = new SongHistory();
            sh.setId(id);
            sh.setUser(userRepository.getReferenceById(userId));
            sh.setSong(songRepository.getReferenceById(songId));
            return sh;
        });

        LocalDateTime now = LocalDateTime.now();

        if (history.getPlayedAt() == null || history.getPlayedAt().isBefore(now.minusMinutes(1))) {
            songRepository.incrementViews(songId);
        }

        history.setPlayedAt(now);
        songHistoryRepository.save(history);
    }
}

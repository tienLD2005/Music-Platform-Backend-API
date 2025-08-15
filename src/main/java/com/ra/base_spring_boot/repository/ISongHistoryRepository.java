package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.dto.resp.SongHistoryResponse;
import com.ra.base_spring_boot.model.SongHistory;
import com.ra.base_spring_boot.model.base.SongHistoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface ISongHistoryRepository extends JpaRepository<SongHistory, SongHistoryId> {

    @Query("""
        SELECT new com.ra.base_spring_boot.dto.resp.SongHistoryResponse(
            s.id,
            s.title,
            CONCAT(a.firstName, ' ', a.lastName),
            al.coverImage,
            s.fileUrl,
            sh.playedAt
        )
        FROM SongHistory sh
        JOIN sh.song s
        JOIN s.artist a
        LEFT JOIN s.album al
        WHERE sh.user.id = :userId
        ORDER BY sh.playedAt DESC
    """)
    Page<SongHistoryResponse> findRecentView(Long userId, Pageable pageable);
}

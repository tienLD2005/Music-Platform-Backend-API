package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CommentEditHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ICommentEditHistoryRepository extends JpaRepository<CommentEditHistory, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CommentEditHistory h WHERE h.comment.id = :commentId")
    void deleteAllByCommentId(@Param("commentId") Long commentId);

    void deleteAllByCommentIdIn(Collection<Long> commentIds); // <-- thêm để xoá subtree

}

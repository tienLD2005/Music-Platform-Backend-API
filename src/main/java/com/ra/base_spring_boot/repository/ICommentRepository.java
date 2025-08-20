package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findBySong_IdAndParentIsNull(Long songId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c")
    Long countAllComments();

    @Query("SELECT c.song.title, COUNT(c.id) as total " +
            "FROM Comment c GROUP BY c.song.title ORDER BY total DESC")
    List<Object[]> findCommentsCountBySong();
}

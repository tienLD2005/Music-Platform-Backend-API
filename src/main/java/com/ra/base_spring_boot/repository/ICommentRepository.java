package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findBySong_IdAndParentIsNull(Long songId, Pageable pageable);
}

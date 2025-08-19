package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ICommentReactionRepository extends JpaRepository<CommentReaction, Integer> {

    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    List<CommentReaction> findAllByComment(Comment comment);

    Long countByComment(Comment comment);

    List<CommentReaction> findByComment(Comment comment);
}

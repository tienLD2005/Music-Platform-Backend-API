package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ICommentReactionRepository extends JpaRepository<CommentReaction, Integer> {

    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    List<CommentReaction> findAllByComment(Comment comment);

    Long countByComment(Comment comment);

    List<CommentReaction> findByComment(Comment comment);

    @Query("SELECT cr.comment FROM CommentReaction cr WHERE cr.reactionEnum = :reaction")
    List<Comment> findReportedComments(ReactionEnum reaction);

    @Query("SELECT cr.comment, COUNT(cr.id) as total " +
            "FROM CommentReaction cr WHERE cr.reactionEnum = :reaction " +
            "GROUP BY cr.comment ORDER BY total DESC")

    List<Object[]> findMostReactedComments(ReactionEnum reaction);
}

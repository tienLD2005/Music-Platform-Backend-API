package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;

import java.util.List;

public interface ICommentReactionService {
    CommentReaction reactToComment(Long commentId, ReactionEnum reactionEnum);
    void removeReaction(Long commentId);
    List<CommentReaction> getReactionsByComment(Long commentId);
    Long countReactions(Long commentId);

    Comment getCommentById(Long commentId);
}

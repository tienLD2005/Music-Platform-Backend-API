package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import com.ra.base_spring_boot.repository.ICommentReactionRepository;
import com.ra.base_spring_boot.repository.ICommentRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.ICommentReactionService;
import com.ra.base_spring_boot.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReactionServiceImpl implements ICommentReactionService {

    private final ICommentReactionRepository reactionRepository;
    private final ICommentRepository commentRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public CommentReaction reactToComment(Long commentId, ReactionEnum reactionEnum) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = getCommentById(commentId);

        return reactionRepository.findByUserAndComment(user, comment)
                .map(existing -> {
                    existing.setReactionEnum(reactionEnum);
                    return reactionRepository.save(existing);
                })
                .orElseGet(() -> {
                    CommentReaction reaction = new CommentReaction();
                    reaction.setUser(user);
                    reaction.setComment(comment);
                    reaction.setReactionEnum(reactionEnum);
                    return reactionRepository.save(reaction);
                });
    }

    @Override
    @Transactional
    public void removeReaction(Long commentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = getCommentById(commentId);
        reactionRepository.findByUserAndComment(user, comment)
                .ifPresent(reactionRepository::delete);
    }

    @Override
    public List<CommentReaction> getReactionsByComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        return reactionRepository.findByComment(comment);
    }

    @Override
    public Long countReactions(Long commentId) {
        Comment comment = getCommentById(commentId);
        return reactionRepository.countByComment(comment);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}

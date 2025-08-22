package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.CommentMapper;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.CommentEditHistory;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ICommentEditHistoryRepository;
import com.ra.base_spring_boot.repository.ICommentRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IClientCommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientCommentServiceImpl implements IClientCommentService {

    private final ICommentRepository commentRepository;
    private final ISongRepository songRepository;
    private final IUserRepository userRepository;
    private final ICommentEditHistoryRepository commentEditHistoryRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    private static final String BAD_WORDS_PATTERN = "(?i).*\\b(chửi|bậy|tục)\\b.*";

    private void validateCommentContent(String content) {
        if (content.matches(BAD_WORDS_PATTERN)) {
            throw new HttpBadRequest("Comment contains bad words");
        }
    }

    @Override
    public PageResponse<CommentResponseDTO> getCommentsBySong(Long songId, int page, int size, String sortBy, String sortDir) {

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        if (page < 0) {
            throw new HttpBadRequest("Page must be greater than 0");
        }

        if (size <= 0) {
            throw new HttpBadRequest("Size must be greater than 0");
        }


        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> commentPage = commentRepository.findBySong_IdAndParentIsNull(songId, pageable);

        if (page >= commentPage.getTotalPages() && commentPage.getTotalPages() > 0) {
            throw new HttpBadRequest("Page must be less than total pages: " + commentPage.getTotalPages());
        }

        return PageResponse.<CommentResponseDTO>builder()
                .content(commentPage.map(CommentMapper::toDto).getContent())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .size(commentPage.getSize())
                .build();
    }

    @Override
    public CommentResponseDTO addComment(CommentRequest request) {

        User user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new HttpNotFound("User not found"));


        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        validateCommentContent(request.getContent());

        Comment comment = Comment.builder()
                .user(user)
                .song(song)
                .content(request.getContent().trim().replaceAll("\\s+", " "))
                .build();

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new HttpNotFound("Parent comment not found"));
            comment.setParent(parent);
        }

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }

    @Override
    public CommentResponseDTO updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpNotFound("Comment not found"));

        if (!comment.getUser().getId().equals(getCurrentUserId())) {
            throw new HttpBadRequest("You can only edit your own comments");
        }

        validateCommentContent(newContent);

            CommentEditHistory history = CommentEditHistory.builder()
                .comment(comment)
                .oldContent(comment.getContent())
                .editedAt(LocalDateTime.now())
                .build();
        commentEditHistoryRepository.save(history);

        comment.setContent(newContent.trim().replaceAll("\\s+", " "));
        Comment updated = commentRepository.save(comment);
        return CommentMapper.toDto(updated);
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpNotFound("Comment not found"));

        if (!comment.getUser().getId().equals(getCurrentUserId())) {
            throw new HttpBadRequest("You can only delete your own comments");
        }

        commentEditHistoryRepository.deleteAllByCommentId(commentId);

        commentRepository.delete(comment);
    }

}

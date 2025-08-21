package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.CommentRequest;
import com.ra.base_spring_boot.dto.resp.CommentResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.CommentMapper;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ICommentEditHistoryRepository;
import com.ra.base_spring_boot.repository.ICommentRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IArtistCommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ArtistCommentServiceImpl implements IArtistCommentService {

    private final ICommentRepository commentRepository;
    private final ISongRepository songRepository;
    private final IUserRepository userRepository;
    private final ICommentEditHistoryRepository commentEditHistoryRepository;

    private static final Set<String> ALLOWED_SORT_BY = Set.of("createdAt", "updatedAt");
    private static final String BAD_WORDS_PATTERN = "(?i).*\\b(chửi|bậy|tục)\\b.*";

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    private void validateCommentContent(String content) {
        if (content == null || content.isBlank()) {
            throw new HttpBadRequest("Content must not be blank");
        }
        if (content.length() < 3 || content.length() > 500) {
            throw new HttpBadRequest("Content length must be between 3 and 500 characters");
        }
        if (content.matches(BAD_WORDS_PATTERN)) {
            throw new HttpBadRequest("Content contains inappropriate words");
        }
    }

    private void ensureSongOwnedByCurrentArtist(Song song) {
        Long currentId = getCurrentUserId();
        if (song.getArtist() == null || !Objects.equals(song.getArtist().getId(), currentId)) {
            throw new HttpBadRequest("You can only manage comments on your own songs");
        }
    }

    @Override
    public PageResponse<CommentResponseDTO> getCommentsForOwnedSong(Long songId, int page, int size, String sortBy, String sortDir) {
        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));
        ensureSongOwnedByCurrentArtist(song);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Comment> commentPage = commentRepository.findBySong_IdAndParentIsNull(songId, pageable);

        int totalPages = commentPage.getTotalPages();
        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
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
    public CommentResponseDTO replyToComment(CommentRequest request) {
        if (request.getParentId() == null) {
            throw new HttpBadRequest("parentId is required for reply");
        }
        validateCommentContent(request.getContent());

        User artist = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new HttpNotFound("Song not found"));
        ensureSongOwnedByCurrentArtist(song);

        Comment parent = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> new HttpNotFound("Parent comment not found"));
        if (!Objects.equals(parent.getSong().getId(), song.getId())) {
            throw new HttpBadRequest("Parent comment does not belong to the provided song");
        }

        Comment reply = Comment.builder()
                .user(artist)
                .song(song)
                .content(request.getContent().trim().replaceAll("\\s+", " "))
                .parent(parent)
                .build();

        Comment saved = commentRepository.save(reply);
        return CommentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCommentAsArtist(Long commentId, boolean confirm) {
        if (!confirm) {
            throw new HttpBadRequest("Deletion not confirmed. Pass confirm=true to proceed");
        }

        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpNotFound("Comment not found"));

        Song song = target.getSong();
        if (song == null) throw new HttpBadRequest("Comment is not linked to any song");
        ensureSongOwnedByCurrentArtist(song);

        List<Long> idsToDelete = new ArrayList<>();
        collectIdsRecursively(target, idsToDelete);

        if (!idsToDelete.isEmpty()) {
            commentEditHistoryRepository.deleteAllByCommentIdIn(idsToDelete);
        }

        commentRepository.delete(target);
    }

    private void collectIdsRecursively(Comment c, List<Long> out) {
        out.add(c.getId());
        if (c.getReplies() != null && !c.getReplies().isEmpty()) {
            for (Comment r : c.getReplies()) {
                collectIdsRecursively(r, out);
            }
        }
    }
}

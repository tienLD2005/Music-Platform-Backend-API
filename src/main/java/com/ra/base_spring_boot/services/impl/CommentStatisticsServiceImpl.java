package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.CommentStatisticsResponseDTO;
import com.ra.base_spring_boot.dto.resp.PopularCommentDTO;
import com.ra.base_spring_boot.dto.resp.ReportedCommentDTO;
import com.ra.base_spring_boot.model.Comment;
import com.ra.base_spring_boot.model.CommentReaction;
import com.ra.base_spring_boot.model.constants.ReactionEnum;
import com.ra.base_spring_boot.repository.ICommentReactionRepository;
import com.ra.base_spring_boot.repository.ICommentRepository;
import com.ra.base_spring_boot.services.ICommentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentStatisticsServiceImpl implements ICommentStatisticsService {

    private final ICommentRepository commentRepository;
    private final ICommentReactionRepository reactionRepository;

    @Override
    public CommentStatisticsResponseDTO getCommentsStatistics() {
        Long totalComments = commentRepository.countAllComments();

        List<ICommentRepository.SongCommentStats> stats = commentRepository.findCommentsCountBySong();

        Map<String, Long> commentsBySong = stats.stream()
                .collect(Collectors.toMap(
                        s ->  s.getSongId() + " - " + s.getTitle(),
                        ICommentRepository.SongCommentStats::getTotal,
                        (u,v) -> u,
                        LinkedHashMap::new
                ));

        List<ReportedCommentDTO> reportedComments = reactionRepository.findReportedComments(ReactionEnum.REPORT)
                .stream()
                .map(c -> new ReportedCommentDTO(c.getId(), c.getContent()))
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> commentsOverTime = commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCreatedAt().format(formatter),
                        Collectors.counting()
                ));

        List<PopularCommentDTO> popularComments = reactionRepository.findMostReactedComments(ReactionEnum.LIKE).stream()
                .limit(3)
                .map(row -> {
                    Comment c = (Comment) row[0];
                    Long likes = (Long) row[1];
                    return new PopularCommentDTO(c.getId(), c.getContent(), likes);
                })
                .toList();

        return CommentStatisticsResponseDTO.builder()
                .totalComments(totalComments)
                .commentsBySong(commentsBySong)
                .reportedComments(reportedComments)
                .commentsOverTime(commentsOverTime)
                .popularComments(popularComments)
                .build();
    }
}

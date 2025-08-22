package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.dto.resp.TopSongOfWeek;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.mapper.SongMapper;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.SongDeleteHistory;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.SongDeleteHistoryRepo;
import com.ra.base_spring_boot.services.ISongService;
import com.ra.base_spring_boot.services.MailService;
import com.ra.base_spring_boot.services.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private final ISongRepository songRepository;
    private final EmailService mailService;
    private final SongDeleteHistoryRepo songDeleteHistoryRepo;

    @Override
    public List<TopSongOfWeek> getTop15SongsOfWeek() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable top15 = PageRequest.of(0, 15);
        return songRepository.findTopSongsOfWeek(sevenDaysAgo, top15);
    }

    @Override
    public List<TopSongDTO> getTopSongsAllTime(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTopSongsAllTime(pageable);
    }

    @Override
    public List<TopSongDTO> getTrendingSongs(int limit) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findTrendingSongs(sevenDaysAgo, pageable);
    }

    @Override
    public PageResponse<SongResponse> getAllSongs(String keyword, Pageable pageable) {
        Page<Song> page;

        if (keyword == null || keyword.isEmpty()) {
            page = songRepository.findAll(pageable);
        } else {
            page = songRepository.findByTitle(keyword, pageable);
        }
        List<SongResponse> responses = page.stream()
                .map(SongMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public void deleteSong(Long songId, String reason, String adminName) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        User artist = song.getArtist();
        songRepository.delete(song);

        String subject = "Notice: The song \"" + song.getTitle() + "\" has been deleted";
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(artist.getLastName()).append(",\n\n");
        body.append("Your song \"").append(song.getTitle())
                .append("\" has been deleted by admin ").append(adminName).append(".\n\n");
        body.append("Reason: ").append(reason).append("\n\n");
        body.append("If you believe this is a mistake, please contact our support team to appeal.\n\n");
        body.append("Best regards,\nThe Administration Team");

        mailService.sendEmail(artist.getEmail(), subject, body.toString());

        SongDeleteHistory history = SongDeleteHistory.builder()
                .songId(song.getId())
                .songTitle(song.getTitle())
                .artistId(artist.getId())
                .deletedAt(LocalDateTime.now())
                .deletedBy(adminName)
                .deleteReason(reason)
                .build();

        songDeleteHistoryRepo.save(history);
    }
}

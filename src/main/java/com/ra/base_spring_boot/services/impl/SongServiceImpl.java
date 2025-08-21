package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.SongDeleteHistory;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.SongDeleteHistoryRepo;
import com.ra.base_spring_boot.services.ISongService;
import com.ra.base_spring_boot.services.MailService;
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
    private final JavaMailSenderImpl mailSender;
    private final SongDeleteHistoryRepo songDeleteHistory;
    private final MailService mailService;
    private final SongDeleteHistoryRepo songDeleteHistoryRepo;

    @Override
    public List<TopSongDTO> getTop15SongsOfWeek() {
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
                .map(this::mapToSongResponse)
                .toList();



        return new PageResponse<>(
                responses,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private SongResponse mapToSongResponse(Song song) {
        return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .duration(song.getDuration())
                .artistId(song.getArtist() != null ? song.getArtist().getId() : null)
                .artistName(song.getArtist() != null ? song.getArtist().getFirstName() +' ' + song.getArtist().getLastName(): null)
                .albumId(song.getAlbum() != null ? song.getAlbum().getId() : null)
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .createdAt(song.getCreatedAt())
                .status(song.getStatus().name())
                .genres(song.getGenres() != null
                        ? song.getGenres().stream().map(Genre::getGenreName).toList()
                        : null)
                .build();
    }


    @Override
    public void deleteSong(Long songId, String reason, String adminName) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Không tìm thấy bài hát"));

        User artist = song.getArtist();
        songRepository.delete(song);

        String subject = "Thông báo: Bài hát \"" + song.getTitle() + "\" đã bị xoá";
        String body = "<p>Xin chào " + artist.getLastName() + ",</p>" +
                "<p>Bài hát <strong>" + song.getTitle() + "</strong> của bạn đã bị xoá bởi admin <b>" + adminName + "</b>.</p>" +
                "<p><b>Lý do:</b> " + reason + "</p>" +
                "<p>Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ để kháng nghị.</p>" +
                "<br/>Trân trọng,<br/>Đội ngũ quản trị";

        mailService.sendEmail(artist.getEmail(), subject, body);

        // Lưu lịch sử xoá
        SongDeleteHistory history = new SongDeleteHistory();
        history.setSongTitle(song.getTitle());
        history.setArtistId(artist.getId());
        history.setReason(reason);
        history.setDeletedBy(adminName);
        history.setDeletedAt(LocalDateTime.now());

        songDeleteHistoryRepo.save(history);
    }


    private void sendDeleteEmail(String to, String songTitle, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Thông báo: Bài hát của bạn đã bị xóa");
            helper.setText(
                    "<p>Xin chào,</p>" +
                            "<p>Bài hát <b>" + songTitle + "</b> đã bị xóa bởi quản trị viên.</p>" +
                            "<p><b>Lý do:</b> " + reason + "</p>" +
                            "<p>Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ hỗ trợ để kháng nghị.</p>" +
                            "<br><p>Trân trọng,<br>Đội ngũ quản trị</p>",
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }

    private void saveDeleteHistory(Song song, String reason) {
        SongDeleteHistory history = SongDeleteHistory.builder()
                .songId(song.getId())
                .songTitle(song.getTitle())
                .artistId(song.getArtist().getId())
                .deletedAt(LocalDateTime.now())
                .deletedBy("ADMIN")
                .deleteReason(reason)
                .build();

        songDeleteHistory.save(history);
    }



}

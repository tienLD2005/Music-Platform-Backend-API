package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.DownloadSongRequest;
import com.ra.base_spring_boot.dto.resp.DownloadResponse;
import com.ra.base_spring_boot.dto.resp.DownloadedSongResponse;
import com.ra.base_spring_boot.mapper.DownloadMapper;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.AlbumType;
import com.ra.base_spring_boot.repository.IDownloadSongRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IDownloadSongService;
import com.ra.base_spring_boot.utils.DownloadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DownloadSongServiceImpl implements IDownloadSongService {

    private final IDownloadSongRepository downloadRepository;
    private final ISongRepository songRepository;
    private final IUserRepository userRepository;
    private final DownloadFile downloadFile;
    private final DownloadMapper downloadMapper;

    private static final String DOWNLOAD_BASE_PATH = "C:\\music\\";

    @Override
    @Transactional
    public DownloadResponse downloadSong(Long userId, DownloadSongRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            Song song = songRepository.findById(request.getSongId())
                    .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

            if (isAlreadyDownloaded(userId, request.getSongId())) {
                return DownloadResponse.builder()
                        .success(false)
                        .message("Bài hát đã được tải về trước đó")
                        .build();
            }

            if (!canDownload(user, song)) {
                return DownloadResponse.builder()
                        .success(false)
                        .message("Bạn cần gói Premium để tải bài hát này")
                        .build();
            }

            String fileName = sanitizeFileName(song.getArtist().getFirstName() + "_" +
                    song.getArtist().getLastName() + "_" +
                    song.getTitle()) + ".mp3";
            String filePath = DOWNLOAD_BASE_PATH + "\\" + fileName;

            boolean downloadSuccess = downloadFile.download(song.getFileUrl(), filePath);

            if (!downloadSuccess) {
                return DownloadResponse.builder()
                        .success(false)
                        .message("Không thể tải file về. Vui lòng thử lại sau.")
                        .build();
            }

            Download download = Download.builder()
                    .user(user)
                    .song(song)
                    .filePath(filePath)
                    .addedAt(LocalDateTime.now())
                    .build();

            downloadRepository.save(download);

            return DownloadResponse.builder()
                    .success(true)
                    .message("Tải bài hát thành công")
                    .filePath(filePath)
                    .build();

        } catch (Exception e) {
            return DownloadResponse.builder()
                    .success(false)
                    .message("Lỗi: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Page<DownloadedSongResponse> getDownloadedSongs(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được null");
        }
        Page<Download> downloads = downloadRepository.findByUserId(userId, pageable);

        List<DownloadedSongResponse> responses = downloads.getContent().stream()
                .map(downloadMapper::mapToDownloadedSongResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, downloads.getTotalElements());
    }

    @Override
    public List<DownloadedSongResponse> getDownloadedSongsSorted(Long userId, String sortBy) {
        List<Download> downloads;

        switch (sortBy.toLowerCase()) {
            case "title":
                downloads = downloadRepository.findByUserIdOrderBySongTitleAsc(userId);
                break;
            case "date":
            default:
                downloads = downloadRepository.findByUserIdOrderByDownloadedAtDesc(userId);
                break;
        }

        return downloads.stream()
                .map(downloadMapper::mapToDownloadedSongResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DownloadResponse removeDownloadedSong(Long userId, Long songId) {
        try {
            Optional<Download> downloadOpt = downloadRepository.findByUserIdAndSongId(userId, songId);

            if (downloadOpt.isEmpty()) {
                return DownloadResponse.builder()
                        .success(false)
                        .message("Bài hát không có trong danh sách đã tải về")
                        .build();
            }

            Download download = downloadOpt.get();

            try {
                if (download.getFilePath() != null) {
                    Path filePath = Paths.get(download.getFilePath());
                    Files.deleteIfExists(filePath);
                }
            } catch (Exception e) {
                System.err.println("Không thể xóa file: " + e.getMessage());
            }

            downloadRepository.delete(download);

            return DownloadResponse.builder()
                    .success(true)
                    .message("Đã xóa bài hát khỏi danh sách tải về")
                    .build();

        } catch (Exception e) {
            return DownloadResponse.builder()
                    .success(false)
                    .message("Lỗi khi xóa bài hát: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean isAlreadyDownloaded(Long userId, Long songId) {
        return downloadRepository.existsByUserIdAndSongId(userId, songId);
    }

    @Override
    public long getDownloadCount(Long userId) {
        return downloadRepository.countByUserId(userId);
    }

    private boolean canDownload(User user, Song song) {
        if (song.getAlbum() != null && song.getAlbum().getType() == AlbumType.FREE) {
            return true;
        }

        Optional<Subscription> activeSubscription = user.getSubscriptions().stream()
                .filter(sub -> sub.getStatus() == com.ra.base_spring_boot.model.constants.Status.ACTIVE)
                .filter(sub -> sub.getEndTime().isAfter(LocalDateTime.now()))
                .findFirst();

        return activeSubscription.isPresent();
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

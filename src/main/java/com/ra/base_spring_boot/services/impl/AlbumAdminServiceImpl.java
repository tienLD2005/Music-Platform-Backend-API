package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.AlbumDeleteRequest;
import com.ra.base_spring_boot.dto.resp.AlbumAdminResponse;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.exception.*;
import com.ra.base_spring_boot.mapper.AlbumAdminMapper;
import com.ra.base_spring_boot.mapper.PageMapper;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.AlbumAuditLog;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import com.ra.base_spring_boot.repository.IAlbumAdminRepository;
import com.ra.base_spring_boot.repository.IAlbumAuditLogRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.IAlbumAdminService;
import com.ra.base_spring_boot.services.email.EmailService;
import com.ra.base_spring_boot.utils.SecurityUtil;
import com.ra.base_spring_boot.validate.ValidateAlbumAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumAdminServiceImpl implements IAlbumAdminService {

    private final IAlbumAdminRepository albumRepository;
    private final IAlbumAuditLogRepository auditLogRepository;
    private final IUserRepository userRepository;
    private final EmailService emailService;
    private final ValidateAlbumAdmin validateAlbumAdmin;

    @Override
    public PageResponse<AlbumAdminResponse> getAllAlbums(String keyword, AlbumStatus status,
                                                         int page, int size, String sortBy, String sortDir) {
        log.info("Fetching albums: keyword={}, status={}, page={}, size={}, sortBy={}, sortDir={}",
                keyword, status, page, size, sortBy, sortDir);

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Album> albumPage = albumRepository.findAlbumsWithFilters(keyword, status, pageable);

        Page<AlbumAdminResponse> responsePage = albumPage.map(album -> {
            Long songCount = albumRepository.countSongsByAlbumId(album.getId());
            return AlbumAdminMapper.toAlbumAdminResponse(album, songCount);
        });

        return PageMapper.toPageResponse(responsePage);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    @Override
    public AlbumAdminResponse getAlbumById(Long id) {
        if (id == null || id <= 0) throw new HttpBadRequest("Invalid album ID");
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
        Long songCount = albumRepository.countSongsByAlbumId(id);
        return AlbumAdminMapper.toAlbumAdminResponse(album, songCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(AlbumDeleteRequest request) {
        validateAlbumAdmin.validateDeleteRequest(request);

        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException(request.getAlbumId()));

        validateAlbumAdmin.validateAlbumForDeletion(album);

        User admin = getCurrentAdmin();

        createAuditLog(admin, album, "DELETE", request.getReason(), request.getAdditionalNotes());

        try {
            emailService.sendAlbumDeletionNotification(
                    album.getArtist().getEmail(),
                    album.getTitle(),
                    request.getReason(),
                    request.getAdditionalNotes()
            );
        } catch (Exception e) {
            throw new HttpConflict("Album deleted but failed to send email: " + e.getMessage());
        }

        albumRepository.delete(album);
        log.info("Album '{}' successfully deleted by admin '{}'", album.getTitle(), admin.getEmail());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbumStatus(Long albumId, AlbumStatus status, String reason) {
        validateAlbumAdmin.validateStatusUpdate(albumId, status);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(albumId));

        AlbumStatus oldStatus = album.getStatus();
        album.setStatus(status);
        albumRepository.save(album);

        User admin = getCurrentAdmin();
        createAuditLog(admin, album, determineAction(status),
                reason != null ? reason : "Status changed from " + oldStatus + " to " + status,
                "Status updated");

        log.info("Album '{}' status updated from {} to {} by admin '{}'",
                album.getTitle(), oldStatus, status, admin.getEmail());
    }

    private User getCurrentAdmin() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new HttpUnAuthorized("Current admin not found"));
    }

    private void createAuditLog(User admin, Album album, String action, String reason, String notes) {
        AlbumAuditLog auditLog = AlbumAuditLog.builder()
                .admin(admin)
                .albumId(album.getId())
                .albumTitle(album.getTitle())
                .artistEmail(album.getArtist().getEmail())
                .action(action)
                .reason(reason)
                .additionalNotes(notes)
                .build();
        auditLogRepository.save(auditLog);
    }

    private String determineAction(AlbumStatus status) {
        return switch (status) {
            case ACTIVE -> "APPROVE";
            case REJECTED -> "REJECT";
            default -> "UPDATE_STATUS";
        };
    }
}

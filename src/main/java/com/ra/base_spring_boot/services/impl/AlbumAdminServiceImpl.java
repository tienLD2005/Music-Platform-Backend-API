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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private ValidateAlbumAdmin validateAlbumAdmin;

    @Override
    public PageResponse<AlbumAdminResponse> getAllAlbums(String keyword, AlbumStatus status,
                                                         int page, int size, String sortBy, String sortDir) {
        try {
            log.info("Fetching albums with keyword: {}, status: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                    keyword, status, page, size, sortBy, sortDir);

            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            Page<Album> albumPage = albumRepository.findAlbumsWithFilters(keyword, status, pageable);
            Page<AlbumAdminResponse> responsePage = albumPage.map(album -> {
                try {
                    Long songCount = albumRepository.countSongsByAlbumId(album.getId());
                    return AlbumAdminMapper.toAlbumAdminResponse(album, songCount);
                } catch (Exception e) {
                    log.error("Error mapping album with ID: {}", album.getId(), e);
                    throw new DatabaseOperationException(
                            "Error processing album data with ID: " + album.getId(), e);
                }
            });
            return PageMapper.toPageResponse(responsePage);
        } catch (DataAccessException e) {
            log.error("Database error when fetching albums", e);
            throw new DatabaseOperationException("Database error when retrieving album list", e);
        } catch (Exception e) {
            log.error("Unexpected error when fetching albums", e);
            throw new RuntimeException("Unexpected error when retrieving album list: " + e.getMessage(), e);
        }
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        try {
            if (page < 0) {
                log.warn("Invalid page number: {}, using default 0", page);
                page = 0;
            }
            if (size <= 0 || size > 100) {
                log.warn("Invalid page size: {}, using default 10", size);
                size = 10;
            }

            if (sortBy == null || sortBy.trim().isEmpty()) {
                sortBy = "createdAt";
            }

            if (sortDir == null || (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))) {
                sortDir = "desc";
            }

            Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            return PageRequest.of(page, size, Sort.by(direction, sortBy));
        } catch (Exception e) {
            log.error("Error creating Pageable object", e);
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
    }


    @Override
    public AlbumAdminResponse getAlbumById(Long id) {
        try {
            log.info("Fetching album with ID: {}", id);

            if (id == null || id <= 0) {
                throw new HttpBadRequest("Invalid album ID");
            }

            Album album = albumRepository.findById(id)
                    .orElseThrow(() -> new AlbumNotFoundException(id));

            Long songCount = albumRepository.countSongsByAlbumId(id);
            return AlbumAdminMapper.toAlbumAdminResponse(album, songCount);

        } catch (AlbumNotFoundException e) {
            log.warn("Album not found with ID: {}", id);
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error when fetching album with ID: {}", id, e);
            throw new DatabaseOperationException("Database error when retrieving album information", e);
        } catch (Exception e) {
            log.error("Unexpected error when fetching album with ID: {}", id, e);
            throw new RuntimeException("Unexpected error when retrieving album information: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(AlbumDeleteRequest request) {
        try {
            log.info("Starting album deletion process for ID: {}", request.getAlbumId());

            // Validate input
            validateAlbumAdmin.validateDeleteRequest(request);

            // Find album
            Album album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new AlbumNotFoundException(request.getAlbumId()));

            // Check if album can be deleted
            validateAlbumAdmin.validateAlbumForDeletion(album);

            // Get current admin
            User admin = getCurrentAdmin();

            // Create audit log before deletion
            createAuditLog(admin, album, "DELETE", request.getReason(), request.getAdditionalNotes());

            // Send notification email
            sendDeletionNotification(album, request);

            // Delete album
            performAlbumDeletion(album);

            log.info("Album {} successfully deleted by admin {}",
                    album.getTitle(), admin.getEmail());

        } catch (AlbumNotFoundException | HttpBadRequest e) {
            log.warn("Validation error during album deletion: {}", e.getMessage());
            throw e;
        } catch (EmailSendException e) {
            log.error("Failed to send deletion notification", e);
            throw new AlbumDeleteException("Album has been deleted but failed to send notification email: " + e.getMessage(), e);
        } catch (AuditLogException e) {
            log.error("Failed to create audit log", e);
            throw new AlbumDeleteException("Unable to create audit log: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Database error during album deletion", e);
            throw new DatabaseOperationException("Database error when deleting album", e);
        } catch (Exception e) {
            log.error("Unexpected error during album deletion", e);
            throw new AlbumDeleteException("Unexpected error when deleting album: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbumStatus(Long albumId, AlbumStatus status, String reason) {
        try {
            log.info("Updating album status for ID: {} to {}", albumId, status);

            // Validate input
            validateAlbumAdmin.validateStatusUpdate(albumId, status);

            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new AlbumNotFoundException(albumId));

            User admin = getCurrentAdmin();
            AlbumStatus oldStatus = album.getStatus();

            // Update status
            album.setStatus(status);
            albumRepository.save(album);

            // Create audit log
            String action = determineAction(status);
            String auditReason = reason != null ? reason : "Status update from " + oldStatus + " to " + status;

            createAuditLog(admin, album, action, auditReason,
                    "Status changed from " + oldStatus + " to " + status);

            log.info("Album {} status updated to {} by admin {}",
                    album.getTitle(), status, admin.getEmail());

        } catch (AlbumNotFoundException | HttpBadRequest e) {
            log.warn("Validation error during status update: {}", e.getMessage());
            throw e;
        } catch (AuditLogException e) {
            log.error("Failed to create audit log for status update", e);
            throw new RuntimeException("Status updated successfully but unable to create audit log: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Database error during status update", e);
            throw new DatabaseOperationException("Database error when updating album status", e);
        } catch (Exception e) {
            log.error("Unexpected error during status update", e);
            throw new RuntimeException("Unexpected error when updating album status: " + e.getMessage(), e);
        }
    }

    private User getCurrentAdmin() {
        try {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            return userRepository.findById(currentUserId)
                    .orElseThrow(() -> new HttpUnAuthorized("Current admin information not found"));
        } catch (RuntimeException e) {
            log.error("Error getting current admin", e);
            throw new HttpUnAuthorized("Admin authentication error: " + e.getMessage());
        }
    }

    private void createAuditLog(User admin, Album album, String action, String reason, String additionalNotes) {
        try {
            AlbumAuditLog auditLog = AlbumAuditLog.builder()
                    .admin(admin)
                    .albumId(album.getId())
                    .albumTitle(album.getTitle())
                    .artistEmail(album.getArtist().getEmail())
                    .action(action)
                    .reason(reason)
                    .additionalNotes(additionalNotes)
                    .build();

            auditLogRepository.save(auditLog);

        } catch (DataAccessException e) {
            log.error("Database error when creating audit log", e);
            throw new AuditLogException("Database error when creating audit log", e);
        } catch (Exception e) {
            log.error("Unexpected error when creating audit log", e);
            throw new AuditLogException("Unexpected error when creating audit log", e);
        }
    }

    private void sendDeletionNotification(Album album, AlbumDeleteRequest request) {
        try {
            emailService.sendAlbumDeletionNotification(
                    album.getArtist().getEmail(),
                    album.getTitle(),
                    request.getReason(),
                    request.getAdditionalNotes()
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid email address: {}", album.getArtist().getEmail(), e);
            throw new EmailSendException("Invalid artist email address: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to send deletion notification", e);
            throw new EmailSendException("Unable to send notification email: " + e.getMessage(), e);
        }
    }

    private void performAlbumDeletion(Album album) {
        try {
            albumRepository.delete(album);
        } catch (DataAccessException e) {
            log.error("Database error when deleting album", e);
            throw new DatabaseOperationException("Database error when deleting album", e);
        } catch (Exception e) {
            log.error("Unexpected error when deleting album", e);
            throw new AlbumDeleteException("Unexpected error when deleting album", e);
        }
    }

    private String determineAction(AlbumStatus status) {
        return switch (status) {
            case ACTIVE -> "APPROVE";
            case REJECTED -> "REJECT";
            default -> "UPDATE_STATUS";
        };
    }
}

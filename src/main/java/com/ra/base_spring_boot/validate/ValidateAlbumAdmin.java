package com.ra.base_spring_boot.validate;

import com.ra.base_spring_boot.dto.req.AlbumDeleteRequest;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import org.springframework.stereotype.Component;

@Component
public class ValidateAlbumAdmin {

    public void validateDeleteRequest(AlbumDeleteRequest request) {
        if (request == null) {
            throw new HttpBadRequest("Album deletion request cannot be empty");
        }
        if (request.getAlbumId() == null || request.getAlbumId() <= 0) {
            throw new HttpBadRequest("Album ID is invalid");
        }
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new HttpBadRequest("Album deletion reason cannot be empty");
        }
        if (request.getReason().length() > 500) {
            throw new HttpBadRequest("Album deletion reason cannot exceed 500 characters");
        }
    }

    public void validateAlbumForDeletion(Album album) {
        if (album.getArtist() == null) {
            throw new HttpBadRequest("Cannot delete album without artist information");
        }
        if (album.getArtist().getEmail() == null || album.getArtist().getEmail().trim().isEmpty()) {
            throw new HttpBadRequest("Cannot send notification because artist has no email");
        }
    }

    public void validateStatusUpdate(Long albumId, AlbumStatus status) {
        if (albumId == null || albumId <= 0) {
            throw new HttpBadRequest("Album ID is invalid");
        }
        if (status == null) {
            throw new HttpBadRequest("Album status cannot be empty");
        }
    }
}

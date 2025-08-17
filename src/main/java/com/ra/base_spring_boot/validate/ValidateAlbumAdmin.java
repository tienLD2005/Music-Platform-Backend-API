package com.ra.base_spring_boot.validate;

import com.ra.base_spring_boot.dto.req.AlbumDeleteRequest;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.constants.AlbumStatus;

public class ValidateAlbumAdmin {

    public void validateDeleteRequest(AlbumDeleteRequest request) {
        if (request == null) {
            throw new HttpBadRequest("Yêu cầu xóa album không được để trống");
        }
        if (request.getAlbumId() == null || request.getAlbumId() <= 0) {
            throw new HttpBadRequest("ID album không hợp lệ");
        }
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new HttpBadRequest("Lý do xóa album không được để trống");
        }
        if (request.getReason().length() > 500) {
            throw new HttpBadRequest("Lý do xóa album không được vượt quá 500 ký tự");
        }
    }

    public void validateAlbumForDeletion(Album album) {
        if (album.getArtist() == null) {
            throw new HttpBadRequest("Không thể xóa album không có thông tin nghệ sĩ");
        }
        if (album.getArtist().getEmail() == null || album.getArtist().getEmail().trim().isEmpty()) {
            throw new HttpBadRequest("Không thể gửi thông báo vì nghệ sĩ không có email");
        }
    }

    public void validateStatusUpdate(Long albumId, AlbumStatus status) {
        if (albumId == null || albumId <= 0) {
            throw new HttpBadRequest("ID album không hợp lệ");
        }
        if (status == null) {
            throw new HttpBadRequest("Trạng thái album không được để trống");
        }
    }
}

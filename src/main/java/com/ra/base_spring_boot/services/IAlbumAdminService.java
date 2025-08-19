package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.AlbumDeleteRequest;
import com.ra.base_spring_boot.dto.resp.AlbumAdminResponse;
import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import org.springframework.data.domain.Pageable;

public interface IAlbumAdminService {

    PageResponse<AlbumAdminResponse> getAllAlbums(String keyword, AlbumStatus status, Pageable pageable);

    AlbumAdminResponse getAlbumById(Long id);

    void deleteAlbum(AlbumDeleteRequest request);

    void updateAlbumStatus(Long albumId, AlbumStatus status, String reason);
}

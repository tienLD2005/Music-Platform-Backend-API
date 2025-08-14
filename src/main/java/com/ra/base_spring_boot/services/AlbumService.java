package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.resp.AlbumResponseDTO;
import com.ra.base_spring_boot.dto.resp.PageResponse;


public interface AlbumService {
    PageResponse<AlbumResponseDTO> getMyAlbums(String title, int page, int size, String sortBy, String sortDir);
    PageResponse<AlbumResponseDTO> getAlbumsByArtist(Long artistId, String title, int page, int size, String sortBy, String sortDir);

    ResponseWrapper<AlbumResponseDTO> createAlbum(AlbumRequest request);
    ResponseWrapper<AlbumResponseDTO> updateAlbum(Long albumId, AlbumRequest request);
    ResponseWrapper<String> deleteAlbum(Long albumId);

}



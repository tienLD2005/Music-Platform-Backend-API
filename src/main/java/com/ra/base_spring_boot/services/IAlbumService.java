package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.AlbumFilter;
import com.ra.base_spring_boot.dto.req.AlbumRequest;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.dto.req.FormSongRequest;
import org.springframework.data.domain.Page;

import java.util.List;


public interface IAlbumService {
    PaginatedResponse<ResponseSong> getSongsByAlbum(Long albumId, int page, int size, String sortBy, String direction);

    ResponseSong addSongToAlbum(Long albumId, FormSongRequest request, String username);
    String deleteSongFromAlbum(Long albumId, Long songId, String name);
    PageResponse<AlbumResponseDTO> getMyAlbums(String title, int page, int size, String sortBy, String sortDir);
    PageResponse<AlbumResponseDTO> getAlbumsByArtist(Long artistId, String title, int page, int size, String sortBy, String sortDir);

    ResponseWrapper<AlbumResponseDTO> createAlbum(AlbumRequest request);
    ResponseWrapper<AlbumResponseDTO> updateAlbum(Long albumId, AlbumRequest request);
    ResponseWrapper<String> deleteAlbum(Long albumId);

    // List Ablum
    PaginatedResponse<AlbumResponse> getAllAlbums(int page, int size, String sortBy, String sortDir, String keyword);
    PaginatedResponse<AlbumResponse> getTopAlbums(String period);
    PaginatedResponse<AlbumResponse> findFeaturedAlbums();
    PaginatedResponse<AlbumResponse> getAlbumsByArtist(Long artistId, int page, int size, String keyword, String sortDir, boolean isPremium);

    List<AlbumResponse> getTopTrendingAlbums(int limit);
}

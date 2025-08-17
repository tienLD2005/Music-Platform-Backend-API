package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Album;
import com.ra.base_spring_boot.model.constants.AlbumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IAlbumAdminRepository extends JpaRepository<Album, Long> {

    @Query("SELECT a FROM Album a WHERE " +
            "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CONCAT(a.artist.firstName, ' ', a.artist.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Album> findAlbumsWithFilters(@Param("keyword") String keyword,
                                      @Param("status") AlbumStatus status,
                                      Pageable pageable);

    @Query("SELECT COUNT(s) FROM Song s WHERE s.album.id = :albumId")
    Long countSongsByAlbumId(@Param("albumId") Long albumId);
}

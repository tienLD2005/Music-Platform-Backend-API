package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.ISongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private final ISongRepository songRepository;

    @Override
    public List<TopSongDTO> getTop15SongsOfWeek() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable top15 = PageRequest.of(0, 15);
        return songRepository.findTopSongsOfWeek(sevenDaysAgo, top15);
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


    //delete
    @Override
    public void deleteSong(Long songId, String reason) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        //  artist
        String artistEmail = song.getArtist().getEmail();

        songRepository.delete(song);

        // commit email->artist
        sendDeleteEmail(artistEmail, song.getTitle(), reason);

        // save
        saveDeleteHistory(song, reason);
    }

    private void sendDeleteEmail(String to, String songTitle, String reason) {
        System.out.println("Sending email to: " + to
                + " | Song: " + songTitle
                + " | Reason: " + reason);
    }

    private void saveDeleteHistory(Song song, String reason) {
        System.out.println("History saved for songId=" + song.getId()
                + ", reason=" + reason);
    }



}

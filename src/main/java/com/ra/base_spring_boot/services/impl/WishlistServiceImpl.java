package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.PageResponse;
import com.ra.base_spring_boot.dto.resp.WishlistResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpConflict;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.SongStatus;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.IWishlistRepository;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {
    private final IUserRepository userRepository;
    private  final ISongRepository songRepository;
    private final IWishlistRepository wishlistRepository;

    @Override
    public String addSongToWishlist(Long songId, MyUserDetails currentUser) {

        User user = userRepository.findById(currentUser.getId()).orElseThrow(()-> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId).orElseThrow(()-> new HttpNotFound("Song not found"));

        if (song.getStatus() != SongStatus.APPROVED) {
            throw new HttpForbidden("Song status is not APPROVED");
        }

        // CHECK DUPLICATE EXIST
        if (user.getWishlistSongs().contains(song)) {
            throw new HttpConflict("Song with this song already exists in the wishlist");
        }

        user.getWishlistSongs().add(song);
        userRepository.save(user);

        return "The song has been successfully added to the wishlist!";
    }

    @Override
    public PageResponse<WishlistResponse> getWishlist(int page, int size, String sortBy, String sortDir, MyUserDetails currentUser) {
        if (page < 0) throw new HttpBadRequest("Page must be >= 0");
        if (size <= 0) throw new HttpBadRequest("Size must be > 0");

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new HttpNotFound("User not found"));

        String sortField = "popular".equalsIgnoreCase(sortBy) ? "views" : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Song> songs = wishlistRepository.findWishlistByUserIdSorted(user.getId(), sortDir, pageable);

        Page<WishlistResponse> wishlists = songs.map(song -> WishlistResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistName(song.getArtist().getFirstName() + " " + song.getArtist().getLastName())
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .duration(song.getDuration())
                .build()
        );

        int totalPages = wishlists.getTotalPages();

        if ((totalPages == 0 && page > 0) || (totalPages > 0 && page >= totalPages)) {
            throw new HttpBadRequest("Page index out of range. totalPages=" + totalPages);
        }

        return PageResponse.<WishlistResponse>builder()
                .content(wishlists.getContent())
                .currentPage(wishlists.getNumber())
                .totalPages(wishlists.getTotalPages())
                .totalElements(wishlists.getTotalElements())
                .size(wishlists.getSize())
                .build();
    }


    @Override
    public String removeFromWishlist(Long songId, MyUserDetails currentUser, boolean confirm) {
        if (!confirm) {
            throw new HttpBadRequest("Deletion not confirmed. Pass confirm=true to proceed");
        }
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        boolean removed = user.getWishlistSongs().removeIf(s -> s.getId().equals(songId));
        if (!removed) {
            throw new HttpBadRequest("You can only remove songs from your own favorites list");
        }

        userRepository.save(user);
        return "Song removed from your favorites!";
    }

}

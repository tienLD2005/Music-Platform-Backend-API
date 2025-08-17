package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.resp.SongResponseDTO;
import com.ra.base_spring_boot.dto.resp.WishlistResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.IRoleRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.IWishlistRepository;
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
    public String addSongToWishlist(Long songId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId).orElseThrow(()-> new HttpNotFound("Song not found"));

        // CHECK DUPLICATE EXIST
        if (user.getWishlistSongs().contains(song)) {
            throw new HttpBadRequest("Song with this song already exists in the wishlist");
        }

        user.getWishlistSongs().add(song);
        userRepository.save(user);

        return "The song has been successfully added to the wishlist!";
    }

    @Override
    public Page<WishlistResponse> getWishlist(int page, int size, String sortBy, String sortDir, Authentication authentication) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);

        Page<Song> songs;
        if ("popular".equalsIgnoreCase(sortBy)) {
            songs = "asc".equalsIgnoreCase(sortDir)
                    ? wishlistRepository.findWishlistOrderByViewsAsc(email, pageable)
                    : wishlistRepository.findWishlistOrderByViewsDesc(email, pageable);
        } else {
            songs = "asc".equalsIgnoreCase(sortDir)
                    ? wishlistRepository.findWishlistOrderByCreatedAtAsc(email, pageable)
                    : wishlistRepository.findWishlistOrderByCreatedAtDesc(email, pageable);
        }

        return songs.map(song -> WishlistResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistName(song.getArtist().getFirstName() + " " + song.getArtist().getLastName())
                .albumName(song.getAlbum() != null ? song.getAlbum().getTitle() : null)
                .fileUrl(song.getFileUrl())
                .views(song.getViews())
                .duration(song.getDuration())
                .build()
        );
    }

    @Override
    public String removeFromWishlist(Long songId, Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId).orElseThrow(()-> new HttpNotFound("Song not found"));

        if (user.getWishlistSongs().remove(song)) {
            userRepository.save(user);
        } else {
            throw  new HttpBadRequest("Song with this song already exists in the wishlist");
        }
        return "Successfully removed the song from the favorites list!!";
    }

}

package com.ra.base_spring_boot.dto.req;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddSongToPlaylistReq {
    @NotNull(message = "Song ID cannot be null")
    private Long songId;
}


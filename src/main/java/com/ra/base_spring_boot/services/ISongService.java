package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.SongResponse;
import com.ra.base_spring_boot.dto.resp.TopSongDTO;

import java.util.List;

public interface ISongService {
    List<TopSongDTO> getTop15SongsOfWeek();

}

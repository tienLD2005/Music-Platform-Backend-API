package com.ra.base_spring_boot.services;

public interface IDownloadSongService {
    boolean downloadSong(String url, String filePath);
}

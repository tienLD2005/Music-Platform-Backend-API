package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.services.IDownloadSongService;
import com.ra.base_spring_boot.utils.DownloadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadSongServiceImpl implements IDownloadSongService {

    private final DownloadFile downloadFile = new DownloadFile();

    @Override
    public boolean downloadSong(String url, String filePath) {
        return downloadFile.download(url, filePath);
    }
}


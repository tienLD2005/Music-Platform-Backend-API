package com.ra.base_spring_boot.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class DownloadFile {
    public boolean download(String url, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            Files.copy(new URL(url).openStream(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Downloaded to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Download failed: " + e.getMessage());
            return false;
        }
    }
}

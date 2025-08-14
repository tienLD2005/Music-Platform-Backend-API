package com.ra.base_spring_boot.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private static final long MAX_AUDIO_SIZE = 20L * 1024 * 1024;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcmtkk2mw",
                "api_key", "663139661167425",
                "api_secret", "zx0Q2GMceFfdaqNkjgllJdqtTC8",
                "secure", true
        ));
    }

    public String uploadAudio(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File không được để trống");
            }
            if (file.getSize() > MAX_AUDIO_SIZE) {
                throw new IllegalArgumentException("Kích thước file tối đa 20MB");
            }

            // Chỉ hỗ trợ MP3 hoặc WAV
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equalsIgnoreCase("audio/mpeg") && // MP3
                            !contentType.equalsIgnoreCase("audio/wav"))) { // WAV
                throw new IllegalArgumentException("Chỉ hỗ trợ file nhạc định dạng MP3 hoặc WAV");
            }

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "songs",
                            "resource_type", "auto"
                    )
            );
            return result.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file lên Cloudinary", e);
        }
    }


}

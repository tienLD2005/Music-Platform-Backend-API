package com.ra.base_spring_boot.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private static final long MAX_AUDIO_SIZE = 20L * 1024 * 1024;
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;

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
                throw new HttpBadRequest("File must not be empty");
            }
            if (file.getSize() > MAX_AUDIO_SIZE) {
                throw new HttpBadRequest("Maximum file size is 20MB");
            }

            String contentType = file.getContentType();
            if (contentType == null ||
                (!contentType.equalsIgnoreCase("audio/mpeg") &&
                 !contentType.equalsIgnoreCase("audio/wav"))) {
                throw new HttpBadRequest("Only MP3 or WAV files are supported");
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
            throw new HttpBadRequest("Error uploading file to Cloudinary: " + e.getMessage());
        }
    }

    public String uploadImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new HttpBadRequest("File must not be empty");
            }
            if (file.getSize() > MAX_IMAGE_SIZE) {
                throw new HttpBadRequest("Maximum image size is 10MB");
            }

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "banners")
            );
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new HttpBadRequest("Error uploading image to Cloudinary: " + e.getMessage());
        }
    }
}

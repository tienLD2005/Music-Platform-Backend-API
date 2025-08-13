package com.ra.base_spring_boot.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcunbzpkh",
                "api_key", "736913266975271",
                "api_secret", "ky1Bx5-5TBNNSg76dNhzm1OeB-E",
                "secure", true
        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File không được để trống");
        if (file.getSize() > MAX_IMAGE_SIZE) throw new IllegalArgumentException("Kích thước ảnh tối đa 10MB");

        Map<?,?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "banners")
        );
        return result.get("secure_url").toString();
    }
}
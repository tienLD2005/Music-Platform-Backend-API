package com.ra.base_spring_boot.services.email;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final IUserRepository userRepository;

    /**
     * Gửi email chỉ khi email tồn tại trong hệ thống (database)
     */
    public void sendEmail(String to, String subject, String text) {
        // 1. Kiểm tra email tồn tại trong DB
        Optional<User> optionalUser = userRepository.findByEmail(to);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Email này không tồn tại trong hệ thống");
        }

        // 2. Tạo email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        // 3. Gửi email
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }
}

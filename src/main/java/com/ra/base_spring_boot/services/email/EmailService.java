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

    public void sendEmail(String to, String subject, String text) {
        Optional<User> optionalUser = userRepository.findByEmail(to);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("This email does not exist in the system");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}

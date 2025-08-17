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

    public void sendAlbumDeletionNotification(String artistEmail, String albumTitle,
                                              String reason, String additionalNotes) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(artistEmail);
            message.setSubject("Thông báo: Album của bạn đã bị xóa - " + albumTitle);

            StringBuilder content = new StringBuilder();
            content.append("Xin chào,\n\n");
            content.append("Chúng tôi xin thông báo rằng album \"").append(albumTitle)
                    .append("\" của bạn đã bị xóa khỏi hệ thống.\n\n");
            content.append("Lý do xóa: ").append(reason).append("\n\n");

            if (additionalNotes != null && !additionalNotes.isEmpty()) {
                content.append("Ghi chú thêm: ").append(additionalNotes).append("\n\n");
            }

            content.append("Nếu bạn có bất kỳ thắc mắc nào hoặc muốn kháng nghị quyết định này, ")
                    .append("vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi.\n\n");
            content.append("Để tránh tình trạng này trong tương lai, vui lòng đảm bảo ")
                    .append("nội dung album tuân thủ tiêu chuẩn cộng đồng của chúng tôi.\n\n");
            content.append("Trân trọng,\nĐội ngũ quản lý Music Platform");

            message.setText(content.toString());
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông báo: " + e.getMessage());
        }
    }
}

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
            message.setSubject("Notice: Your Album Has Been Deleted - " + albumTitle);

            StringBuilder content = new StringBuilder();
            content.append("Dear Artist,\n\n");
            content.append("We are writing to inform you that your album \"").append(albumTitle)
                    .append("\" has been removed from our platform.\n\n");
            content.append("Reason for deletion: ").append(reason).append("\n\n");

            if (additionalNotes != null && !additionalNotes.isEmpty()) {
                content.append("Additional notes: ").append(additionalNotes).append("\n\n");
            }

            content.append("If you have any questions or would like to appeal this decision, ")
                    .append("please contact our support team.\n\n");
            content.append("To avoid this situation in the future, please ensure that ")
                    .append("your album content complies with our community guidelines.\n\n");
            content.append("Best regards,\nMusic Platform Management Team");

            message.setText(content.toString());
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Unable to send notification email: " + e.getMessage());
        }
    }
}

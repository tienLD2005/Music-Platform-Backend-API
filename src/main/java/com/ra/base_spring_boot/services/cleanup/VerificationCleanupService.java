package com.ra.base_spring_boot.services.cleanup;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationCleanupService {

    private final IUserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void clearExpiredVerificationCodes() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAll();

        users.stream()
                .filter(u -> u.getVerificationCode() != null
                        && u.getCreatedAt().isBefore(now.minusMinutes(10)))
                .forEach(u -> {
                    u.setVerificationCode(null);
                    userRepository.save(u);
                });
    }
}

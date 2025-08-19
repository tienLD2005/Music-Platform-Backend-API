package com.ra.base_spring_boot.services.cleanup;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountCleanupService {

    private final IUserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredUnverifiedAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<User> expiredUsers = userRepository
                .findByStatusAndAccountExpirationBefore(UStatus.VERIFY, now);

        if (!expiredUsers.isEmpty()) {
            userRepository.deleteAll(expiredUsers);
            System.out.println("Deleted " + expiredUsers.size() + " expired unverified accounts");
        }
    }
}

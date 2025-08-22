package com.ra.base_spring_boot.services.cleanup;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCleanupService {

    private final IUserRepository userRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredUnverifiedAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<User> expiredUsers = userRepository
                .findByStatusAndAccountExpirationBefore(UStatus.VERIFY, now);

        if (!expiredUsers.isEmpty()) {
            userRepository.deleteAll(expiredUsers);
            log.info("Deleted {} expired unverified accounts", expiredUsers.size());
        }
    }
}

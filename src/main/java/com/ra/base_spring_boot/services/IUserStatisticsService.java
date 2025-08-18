package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IUserStatisticsService {

    private final IUserRepository userRepository;

    public Map<String, Long> getUserCountByStatus() {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> data = userRepository.countUsersByStatus();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return result;
    }

    public Map<String, Long> getUserCountByAccountType() {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> data = userRepository.countUsersByAccountType();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return result;
    }
}
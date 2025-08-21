package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.IGenreStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenreStatisticServiceImpl implements IGenreStatisticService {
    private final ISongRepository songRepository;

    @Override
    public Map<String, Object> getGenreStatistics() {
        Map<String, Object> result = new HashMap<>();

        // Count song by genre
        List<Object[]> songCounts = songRepository.countSongsByGenre();
        Map<String, Long> songCountMap = new HashMap<>();
        for (Object[] row : songCounts) {
            songCountMap.put((String) row[0], (Long) row[1]);
        }
        // the most genre
        List<Object[]> playCounts = songRepository.countPlaysByGenre();
        String mostPlayedGenre = playCounts.isEmpty() ? null : (String) playCounts.get(0)[0];

        result.put("songCountByGenre", songCountMap);
        result.put("mostPlayedGenre", mostPlayedGenre);

        return result;
    }
}

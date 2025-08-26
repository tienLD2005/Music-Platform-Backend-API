package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.services.IGenreStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreStatisticServiceImpl implements IGenreStatisticService {
    private final ISongRepository songRepository;

    @Override
    public Map<String, Object> getGenreStatistics() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Long> songCountByGenre = songRepository.countSongsByGenre().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));

        List<Object[]> playCounts = songRepository.countPlaysByGenre();
        String mostPlayedGenre = playCounts.isEmpty() ? null : (String) playCounts.get(0)[0];

        result.put("songCountByGenre", songCountByGenre);
        result.put("mostPlayedGenre", mostPlayedGenre);

        return result;
    }
}

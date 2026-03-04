package com.recruitment.service;

import com.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public Map<String, Object> getMonthlyGrowth() {
        Map<String, Object> growth = new HashMap<>();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        growth.put("newUsers", userRepository.countByCreatedAtAfter(thirtyDaysAgo));
        growth.put("newJobs", jobRepository.countByCreatedAtAfter(thirtyDaysAgo));
        growth.put("newApplications", applicationRepository.countByCreatedAtAfter(thirtyDaysAgo));

        return growth;
    }

    public List<Map<String, Object>> getDailyUserRegistrations(int days) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().minusDays(i).withHour(23).withMinute(59).withSecond(59);

            Map<String, Object> point = new HashMap<>();
            point.put("date", startOfDay.toLocalDate().toString());
            // Since we don't have a direct countByBetween, we'll use a simplified mock data
            // logic for now
            // In a real app we'd add findByCreatedAtBetween
            point.put("count",
                    userRepository.countByCreatedAtAfter(startOfDay) - userRepository.countByCreatedAtAfter(endOfDay));
            data.add(point);
        }
        return data;
    }

    public Map<String, Long> getApplicationStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", applicationRepository.count());
        // Add more specific aggregations as needed
        return stats;
    }
}

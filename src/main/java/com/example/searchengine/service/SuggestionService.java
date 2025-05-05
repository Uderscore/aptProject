package com.example.searchengine.service;

import com.example.searchengine.model.QueryLog;
import com.example.searchengine.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class SuggestionService {

    private static final String SUGGESTION_KEY = "suggestions";
    private final QueryLogRepository queryLogRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public SuggestionService(QueryLogRepository queryLogRepository, RedisTemplate<String, String> redisTemplate) {
        this.queryLogRepository = queryLogRepository;
        this.redisTemplate = redisTemplate;
    }

//    @Scheduled(cron = "${suggestion.sync.cron}")
    public void syncSuggestions() {
        String tempKey = SUGGESTION_KEY + "_temp";

        // Populate temporary key
        List<QueryLog> popularQueries = queryLogRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .limit(1000)
                .toList();

        // Add to temporary Redis sorted set
        popularQueries.forEach(log ->
                redisTemplate.opsForZSet().add(tempKey, log.getQuery(), log.getCount()));

        // Atomic rename (replace old with new)
        redisTemplate.rename(tempKey, SUGGESTION_KEY);
    }

    public List<String> getSuggestions(String prefix, int limit) {

        if (prefix == null || prefix.isEmpty()) {
            return List.of();
        }
        // Fetch suggestions from Redis
        Set<String> suggestions = redisTemplate.opsForZSet()
                .rangeByScore(SUGGESTION_KEY, 0, Double.MAX_VALUE);

        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }

        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
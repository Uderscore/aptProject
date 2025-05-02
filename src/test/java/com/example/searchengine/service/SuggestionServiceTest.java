package com.example.searchengine.service;

import com.example.searchengine.repository.QueryLogRepository;
import org.testng.annotations.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SuggestionServiceTest {

    @Mock
    private QueryLogRepository queryLogRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private SuggestionService suggestionService;

    @Test
    public void testGetSuggestions() {
        // Mock Redis behavior
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.rangeByScore(eq("suggestions"), anyDouble(), anyDouble()))
                .thenReturn(Set.of("github", "git tutorial", "python"));

        // Test
        List<String> suggestions = suggestionService.getSuggestions("git", 2);
        assertEquals(2, suggestions.size());
        assertEquals("github", suggestions.get(0));
        assertEquals("git tutorial", suggestions.get(1));
    }
}
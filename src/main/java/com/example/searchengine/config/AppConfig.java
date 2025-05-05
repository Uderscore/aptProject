package com.example.searchengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.beans.factory.annotation.Qualifier;
import ranker.rankers.Ranker;

import java.time.Duration;
import java.util.HashMap;

import java.util.Map;

@Configuration
public class AppConfig implements CachingConfigurer {

    @Bean
    public Ranker oldRanker() {
        return new Ranker();
    }
}

//@Configuration
//public class AppConfig implements CachingConfigurer {
//    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
//                                     @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
//        // Configure serialization using Jackson instead of JDK serialization
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
//
//        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
//
//        // Define cache configurations with different TTLs
//        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//
//        // Cache search results for 30 minutes
//        cacheConfigurations.put("searchResults",
//                defaultConfig.entryTtl(Duration.ofMinutes(30)));
//
//        // Cache term data for 1 hour
//        cacheConfigurations.put("termCache",
//                defaultConfig.entryTtl(Duration.ofHours(1)));
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(defaultConfig)
//                .withInitialCacheConfigurations(cacheConfigurations)
//                .build();
//    }
//
//    @Bean
//    public ObjectMapper cacheObjectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        // Add any specific configurations needed for serialization
//        return mapper;
//    }
//
//    @Override
//    @Bean
//    public CacheErrorHandler errorHandler() {
//        return new SimpleCacheErrorHandler() {
//            @Override
//            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
//                log.warn("Error getting from cache: {}, key: {}", cache.getName(), key, exception);
//                // Continue execution without failing
//            }
//
//            @Override
//            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
//                log.warn("Error putting in cache: {}, key: {}", cache.getName(), key, exception);
//                // Continue execution without failing
//            }
//        };
//    }
//}
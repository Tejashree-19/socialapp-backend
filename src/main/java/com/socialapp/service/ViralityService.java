package com.socialapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViralityService {

    private final RedisTemplate<String, Long> redisTemplate;

    // 🔥 Update score based on action
    public void updateScore(Long postId, String action) {

        String key = "post:" + postId + ":virality_score";

        // ✅ Ensure key exists as numeric
        if (redisTemplate.opsForValue().get(key) == null) {
            redisTemplate.opsForValue().set(key, 0L);
        }

        long value = switch (action) {
            case "LIKE" -> 10L;
            case "COMMENT" -> 5L;
            case "BOT_REPLY" -> 1L;
            default -> 0L;
        };

        redisTemplate.opsForValue().increment(key, value);
    }

    // 🚀 Get current virality score
    public Long getScore(Long postId) {
        String key = "post:" + postId + ":virality_score";
        return redisTemplate.opsForValue().get(key);
    }
}
package com.socialapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, Long> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public void handleBotInteraction(Long userId, String message) {

        String cooldownKey = "notif:cooldown:user_" + userId;

        Boolean exists = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(exists)) {

            String listKey = "user:" + userId + ":pending_notifs";
            stringRedisTemplate.opsForList().rightPush(listKey, message);

        } else {

            System.out.println("Push Notification Sent: " + message);

            redisTemplate.opsForValue()
            .set(cooldownKey, 1L, Duration.ofMinutes(15));
        }
    }
}
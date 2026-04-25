package com.socialapp.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(fixedRate = 300000)
    public void processNotifications() {

        String pattern = "user:*:pending_notifs";

        var keys = stringRedisTemplate.keys(pattern);

        if (keys == null) return;

        for (String key : keys) {

            List<String> messages = stringRedisTemplate.opsForList().range(key, 0, -1);

            if (messages != null && !messages.isEmpty()) {

                System.out.println("📢 Summary: " + messages.size() + " interactions");

                stringRedisTemplate.delete(key);
            }
        }
    }
}
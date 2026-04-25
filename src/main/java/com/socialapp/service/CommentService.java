package com.socialapp.service;

import com.socialapp.entity.Comment;
import com.socialapp.exception.TooManyRequestsException;
import com.socialapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ViralityService viralityService;
    private final RedisTemplate<String, Long> redisTemplate;
    private final NotificationService notificationService;

    public Comment addComment(Long postId, Comment comment) {

        // 🔒 Depth check
        if (comment.getDepthLevel() > 20) {
            throw new RuntimeException("Max depth exceeded");
        }

        boolean isBot = comment.getAuthorId() > 1000;

        if (isBot) {

            // 🔑 1. COOLDOWN CHECK
            String cooldownKey = "cooldown:bot_" + comment.getAuthorId() + ":post_" + postId;

            Boolean exists = redisTemplate.hasKey(cooldownKey);
            if (Boolean.TRUE.equals(exists)) {
                throw new TooManyRequestsException("Bot cooldown active ⏱️");
            }

            // 🔥 2. ATOMIC LIMIT (NO RACE CONDITION)
            String botCountKey = "post:" + postId + ":bot_count";

            Long count = redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Long>) connection -> {

                byte[] key = botCountKey.getBytes();

                return (Long) connection.eval(
                        """
                        local current = redis.call('INCR', KEYS[1])
                        if tonumber(current) > 100 then
                            return -1
                        else
                            return current
                        end
                        """.getBytes(),
                        org.springframework.data.redis.connection.ReturnType.INTEGER,
                        1,
                        key
                );
            });

            if (count == null || count == -1) {
                throw new TooManyRequestsException("Too many bot replies 🚫");
            }

            // ⏱️ 3. SET COOLDOWN (ONLY AFTER SUCCESS)
            redisTemplate.opsForValue()
                    .set(cooldownKey, 1L, Duration.ofMinutes(10));

            // 🔔 4. NOTIFICATION
            notificationService.handleBotInteraction(
                    postId,
                    "Bot " + comment.getAuthorId() + " replied to your post"
            );
        }

        // 💾 5. SAVE TO DATABASE (ONLY AFTER REDIS PASS)
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        // 🔥 6. UPDATE VIRALITY
        viralityService.updateScore(postId, isBot ? "BOT_REPLY" : "COMMENT");

        return saved;
    }
}
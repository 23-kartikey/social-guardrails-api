package com.assignment.social_guardrails_api.scheduler;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationScheduler {

    private final Logger logger=LoggerFactory.getLogger(NotificationScheduler.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public NotificationScheduler(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void processNotifications() {

        String usersSetKey = "pending:notif:users";

        Set<Object> users = redisTemplate.opsForSet().members(usersSetKey);

        if (users == null || users.isEmpty()) return;

        for (Object userObj : users) {

            Long userId = Long.valueOf(userObj.toString());
            String listKey = "user:" + userId + ":pending_notifs";

            Long count = redisTemplate.opsForList().size(listKey);

            if (count != null && count > 0) {

                List<Object> messages = redisTemplate.opsForList()
                    .range(listKey, 0, -1);

                logger.info()

                redisTemplate.delete(listKey);
            }

            redisTemplate.opsForSet().remove(usersSetKey, userId);
        }
    }
}

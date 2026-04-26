package com.assignment.social_guardrails_api.scheduler;

import java.util.List;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.service.NotificationService;

@Service
public class NotificationScheduler {

    private final StringRedisTemplate redisTemplate;
    private final NotificationService notificationService;

    public NotificationScheduler(StringRedisTemplate redisTemplate, NotificationService notificationService){
        this.redisTemplate=redisTemplate;
        this.notificationService=notificationService;
    }

    @Scheduled(fixedRate=300000)
    public void notificationSweep(){
        Set<String> keys=redisTemplate.keys("user:*:pending_notifs");

        for(String key: keys){
            List<String> messages=redisTemplate.opsForList()
                        .range(key, 0, -1);
            redisTemplate.delete(key);
            int size=messages.size();
            String botName=messages.get(0).split(" ")[0];
            notificationService.sendNotification("Summarized Push Notification: "+botName+" and "+(size-1)+" others interacted with your posts");
        }
    }

}

package com.assignment.social_guardrails_api.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger=LoggerFactory.getLogger(UserService.class);
    private final StringRedisTemplate redisTemplate;

    public NotificationService(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }
    
    public void handleNotification(Long userId, String message){
        Boolean hasCooldown=redisTemplate.hasKey("user:"+userId+":notif_cooldown");
        if(Boolean.FALSE.equals(hasCooldown)){
            sendNotification(message);
            redisTemplate.opsForValue()
                    .set("user:"+userId+":notif_cooldown", "1", 15, TimeUnit.MINUTES);
        }
        else{
            redisTemplate.opsForList()
                    .rightPush("user:"+userId+":pending_notifs", message);
        }
    }

    public void sendNotification(String message){
        logger.info(message);
    }

}

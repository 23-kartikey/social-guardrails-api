package com.assignment.social_guardrails_api.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViralityService {
    
    private final StringRedisTemplate redisTemplate;

    public ViralityService(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public void increaseScore(Long postId, int points){
        String key="post:"+postId+":virality_score";
        redisTemplate.opsForValue().increment(key, points);
    }

}

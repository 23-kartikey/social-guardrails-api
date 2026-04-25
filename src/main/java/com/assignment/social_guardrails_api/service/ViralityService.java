package com.assignment.social_guardrails_api.service;

import org.springframework.data.redis.core.RedisTemplate;

public class ViralityService {
    
    private final RedisTemplate<String, Object> redisTemplate;

    public ViralityService(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public void increaseScore(Long postId, int points){
        String key="post:"+postId+":virality_score";
        redisTemplate.opsForValue().increment(key, points);
    }

}

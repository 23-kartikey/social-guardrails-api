package com.assignment.social_guardrails_api.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.BotRequest;
import com.assignment.social_guardrails_api.dto.BotResponse;
import com.assignment.social_guardrails_api.entity.Bot;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.exception.AuthorNotFoundException;
import com.assignment.social_guardrails_api.exception.BotCommentLimitException;
import com.assignment.social_guardrails_api.repository.BotRepository;

@Service
public class BotService {
    
    private BotRepository repo;
    private StringRedisTemplate redisTemplate;

    public BotService(BotRepository repo, StringRedisTemplate redisTemplate){
        this.repo=repo;
        this.redisTemplate=redisTemplate;
    }

    public BotResponse createBot(BotRequest bot){
        return toBotResponse(repo.save(toBot(bot)));
    }

    private BotResponse toBotResponse(Bot bot){
        return new BotResponse(bot.getId(), bot.getName(), bot.getPersonaDescription());
    }

    private Bot toBot(BotRequest req){
        Bot bot=new Bot();

        bot.setName(req.getName());
        bot.setPersonaDescription(req.getPersonaDescription());
        return bot;
    }

    public String getName(Long botId){
        return repo.findById(botId).orElseThrow(()->new AuthorNotFoundException(botId)).getName();
    }

    //redis methods

    public boolean canBotReply(Long postId){
        String key="post:"+postId+":bot_count";
        Long count=redisTemplate.opsForValue().increment(key);
        return count<=100;
    }

    public boolean checkCooldown(Long botId, Post post){
        Long userId=post.getAuthorId();
        String key="cooldown:bot_"+botId+":user_"+userId;
        if(redisTemplate.hasKey(key)){
            throw new BotCommentLimitException();
        }
        redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
        return true;
    }

}

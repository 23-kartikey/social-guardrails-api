package com.assignment.social_guardrails_api.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.UserRequest;
import com.assignment.social_guardrails_api.dto.UserResponse;
import com.assignment.social_guardrails_api.entity.User;
import com.assignment.social_guardrails_api.exception.AuthorNotFoundException;
import com.assignment.social_guardrails_api.repository.UserRepository;

@Service
public class UserService {
    
    private static final Logger logger=LoggerFactory.getLogger(UserService.class);
    private final UserRepository repo;
    private RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository repo, RedisTemplate<String, Object> redisTemplate){
        this.repo=repo;
        this.redisTemplate=redisTemplate;
    }

    public UserResponse createUser(UserRequest user){
        return toUserResponse(repo.save(toUser(user)));
    }

    private User toUser(UserRequest req){
        User user=new User();
        user.setUsername(req.getUsername());
        user.setPremium(req.isPremium());
        return user;
    }

    private UserResponse toUserResponse(User user){
        return new UserResponse(user.getId(), user.getUsername(), user.isPremium());
    }

    public void handleBotNotification(Long userId, String message) {

        String cooldownKey = "user:" + userId + ":notif_cooldown";
        String listKey = "user:" + userId + ":pending_notifs";
        String usersSetKey = "pending:notif:users";

        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(hasCooldown)) {
            redisTemplate.opsForList().rightPush(listKey, message);

            redisTemplate.opsForSet().add(usersSetKey, userId);

        }
        
        else {
            User user=repo.findById(userId).orElseThrow(()->new AuthorNotFoundException(userId));
            logger.info("Notification for "+user.getUsername()+": "+message);

            redisTemplate.opsForValue()
                .set(cooldownKey, "1", 15, TimeUnit.MINUTES);
        }
    }

}

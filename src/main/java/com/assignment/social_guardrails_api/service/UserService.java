package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.UserRequest;
import com.assignment.social_guardrails_api.dto.UserResponse;
import com.assignment.social_guardrails_api.entity.User;
import com.assignment.social_guardrails_api.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo=repo;
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

}

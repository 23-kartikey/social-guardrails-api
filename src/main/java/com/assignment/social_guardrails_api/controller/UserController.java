package com.assignment.social_guardrails_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.social_guardrails_api.dto.UserRequest;
import com.assignment.social_guardrails_api.dto.UserResponse;
import com.assignment.social_guardrails_api.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService service;

    public UserController(UserService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(user));
    }
}

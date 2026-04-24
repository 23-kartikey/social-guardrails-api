package com.assignment.social_guardrails_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.social_guardrails_api.dto.PostRequest;
import com.assignment.social_guardrails_api.dto.PostResponse;
import com.assignment.social_guardrails_api.service.PostService;

@RestController
@RequestMapping(path="/api/posts", produces="application/json")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService=postService;
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> newPost(@RequestBody PostRequest post){
        return ResponseEntity.ok(postService.newPost(post));
    }
    
}

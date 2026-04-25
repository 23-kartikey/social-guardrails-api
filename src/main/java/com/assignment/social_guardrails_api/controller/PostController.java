package com.assignment.social_guardrails_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.social_guardrails_api.dto.CommentRequest;
import com.assignment.social_guardrails_api.dto.CommentResponse;
import com.assignment.social_guardrails_api.dto.PostRequest;
import com.assignment.social_guardrails_api.dto.PostResponse;
import com.assignment.social_guardrails_api.service.CommentService;
import com.assignment.social_guardrails_api.service.PostService;

@RestController
@RequestMapping(path="/api/posts", produces="application/json")
public class PostController {

    private final PostService postService;
    private final CommentService commService;

    public PostController(PostService postService, CommentService commService){
        this.postService=postService;
        this.commService=commService;
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> newPost(@RequestBody PostRequest post){
        return ResponseEntity.ok(postService.newPost(post));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> newComment(@PathVariable Long postId, @RequestBody CommentRequest comment){
        return ResponseEntity.ok(commService.createComment(comment, postId));
    }
    
}

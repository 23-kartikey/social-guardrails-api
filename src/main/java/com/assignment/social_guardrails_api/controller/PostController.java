package com.assignment.social_guardrails_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.social_guardrails_api.dto.CommentRequest;
import com.assignment.social_guardrails_api.dto.CommentResponse;
import com.assignment.social_guardrails_api.dto.PostRequest;
import com.assignment.social_guardrails_api.dto.PostResponse;
import com.assignment.social_guardrails_api.service.CommentService;
import com.assignment.social_guardrails_api.service.LikeService;
import com.assignment.social_guardrails_api.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path="/api/posts", produces="application/json")
public class PostController {

    private final PostService postService;
    private final CommentService commService;
    private final LikeService likeService;

    public PostController(PostService postService, CommentService commService, LikeService likeService){
        this.postService=postService;
        this.commService=commService;
        this.likeService=likeService;
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest post){
        return ResponseEntity.ok(postService.createPost(post));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest comment){
        return ResponseEntity.ok(commService.createComment(comment, postId));
    }

    @PostMapping("/{postId}/like")
        public ResponseEntity<?> createLike(@PathVariable Long postId, @RequestParam Long userId){
            return ResponseEntity.status(201).body(likeService.createLike(postId, userId));
        }
    
}

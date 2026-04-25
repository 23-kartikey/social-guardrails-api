package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.PostRequest;
import com.assignment.social_guardrails_api.dto.PostResponse;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.exception.AuthorNotFoundException;
import com.assignment.social_guardrails_api.repository.AuthorRepository;
import com.assignment.social_guardrails_api.repository.PostRepository;

@Service
public class PostService{

    private final PostRepository postRepo;
    private final AuthorRepository authRepo;

    public PostService(PostRepository postRepo, AuthorRepository authRepo){
        this.postRepo=postRepo;
        this.authRepo=authRepo;
    }

    public PostResponse createPost(PostRequest post){
        authRepo.findById(post.getAuthorId()).orElseThrow(()->new AuthorNotFoundException(post.getAuthorId()));
        return toPostResponse(postRepo.save(toPost(post)));
    }

    private Post toPost(PostRequest req){
        Post post=new Post();
        post.setAuthorId(req.getAuthorId());
        post.setContent(req.getContent());
        return post;
    }

    private PostResponse toPostResponse(Post post){
        return new PostResponse(post.getId(), post.getAuthorId(), post.getContent());
    }

}
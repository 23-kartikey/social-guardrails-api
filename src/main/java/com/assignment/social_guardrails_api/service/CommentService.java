package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.CommentRequest;
import com.assignment.social_guardrails_api.dto.CommentResponse;
import com.assignment.social_guardrails_api.entity.Comment;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.exception.PostNotFoundException;
import com.assignment.social_guardrails_api.repository.CommentRepository;
import com.assignment.social_guardrails_api.repository.PostRepository;

@Service
public class CommentService {
    
    private final CommentRepository commRepo;
    private final PostRepository postRepo;

    public CommentService(CommentRepository commRepo, PostRepository postRepo){
        this.commRepo=commRepo;
        this.postRepo=postRepo;
    }

    public CommentResponse createComment(CommentRequest req, Long postId){
        return toCommentResponse(commRepo.save(toComment(req, postId)));
    }

    private CommentResponse toCommentResponse(Comment comment){
        return new CommentResponse(comment.getId(), comment.getAuthorId(), comment.getContent());
    }

    private Comment toComment(CommentRequest req, Long postId){
        Post post=postRepo.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
        return Comment.builder().authorId(req.getAuthorId()).content(req.getContent()).post(post).build();

    }

}

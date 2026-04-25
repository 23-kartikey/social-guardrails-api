package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.CommentRequest;
import com.assignment.social_guardrails_api.dto.CommentResponse;
import com.assignment.social_guardrails_api.entity.Comment;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.exception.PostNotFoundException;
import com.assignment.social_guardrails_api.repository.BotRepository;
import com.assignment.social_guardrails_api.repository.CommentRepository;
import com.assignment.social_guardrails_api.repository.PostRepository;
import com.assignment.social_guardrails_api.repository.UserRepository;

@Service
public class CommentService {
    
    private final CommentRepository commRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final BotRepository botRepo;
    private final BotService botService;
    private final ViralityService viralityService;

    public CommentService(CommentRepository commRepo, PostRepository postRepo, ViralityService viralityService, BotRepository botRepo, UserRepository userRepo, BotService botService){
        this.commRepo=commRepo;
        this.postRepo=postRepo;
        this.botRepo=botRepo;
        this.userRepo=userRepo;
        this.botService=botService;
        this.viralityService=viralityService;
    }

    public CommentResponse createComment(CommentRequest req, Long postId){
        Comment comment=toComment(req, postId);
        if(userRepo.existsById(req.getAuthorId())){
            viralityService.increaseScore(postId, 50);
            commRepo.save(comment);
        }
        else{
            if(botService.canBotReply(postId) && botService.checkCooldown(comment.getAuthorId(), comment.getPost())){
                viralityService.increaseScore(postId, 1);
                commRepo.save(comment);
            }
        }
        return toCommentResponse(comment);
    }

    private CommentResponse toCommentResponse(Comment comment){
        return new CommentResponse(comment.getId(), comment.getAuthorId(), comment.getContent());
    }

    private Comment toComment(CommentRequest req, Long postId){
        Post post=postRepo.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
        return Comment.builder().authorId(req.getAuthorId()).content(req.getContent()).post(post).build();

    }

}

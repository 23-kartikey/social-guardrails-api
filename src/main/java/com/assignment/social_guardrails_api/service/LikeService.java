package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.entity.Like;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.entity.User;
import com.assignment.social_guardrails_api.exception.AlreadyLikedException;
import com.assignment.social_guardrails_api.exception.AuthorNotFoundException;
import com.assignment.social_guardrails_api.exception.IllegalAuthorException;
import com.assignment.social_guardrails_api.exception.PostNotFoundException;
import com.assignment.social_guardrails_api.repository.BotRepository;
import com.assignment.social_guardrails_api.repository.LikeRepository;
import com.assignment.social_guardrails_api.repository.PostRepository;
import com.assignment.social_guardrails_api.repository.UserRepository;

@Service
public class LikeService {
    
    private final LikeRepository likeRepo;
    private final UserRepository userRepo;
    private final BotRepository botRepo;
    private final PostRepository postRepo;
    private final ViralityService viralityService;

    public LikeService(LikeRepository likeRepo, UserRepository userRepo, BotRepository botRepo, PostRepository postRepo, ViralityService viralityService){
        this.likeRepo=likeRepo;
        this.postRepo=postRepo;
        this.userRepo=userRepo;
        this.viralityService=viralityService;
        this.botRepo=botRepo;
    }

    public String createLike(Long postId, Long userId){
        
        if(botRepo.existsById(userId)) throw new IllegalAuthorException("Bots can't like posts");

        if(likeRepo.existsByPostIdAndUserId(postId, userId)){
            throw new AlreadyLikedException(postId, userId);
        }
        Post post=postRepo.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
        User user=userRepo.findById(userId).orElseThrow(()->new AuthorNotFoundException(userId));

        Like like=new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepo.save(like);
        viralityService.increaseScore(postId, 20);

        return "Like Created";
    }

}   

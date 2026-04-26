package com.assignment.social_guardrails_api.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.CommentRequest;
import com.assignment.social_guardrails_api.dto.CommentResponse;
import com.assignment.social_guardrails_api.entity.Comment;
import com.assignment.social_guardrails_api.entity.Post;
import com.assignment.social_guardrails_api.entity.User;
import com.assignment.social_guardrails_api.exception.CommentDepthLimitReachedException;
import com.assignment.social_guardrails_api.exception.PostNotFoundException;
import com.assignment.social_guardrails_api.repository.CommentRepository;
import com.assignment.social_guardrails_api.repository.PostRepository;
import com.assignment.social_guardrails_api.repository.UserRepository;

@Service
public class CommentService {
    
    private final CommentRepository commRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final BotService botService;
    private final ViralityService viralityService;
    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;

    public CommentService(CommentRepository commRepo, PostRepository postRepo, ViralityService viralityService, UserRepository userRepo, BotService botService, NotificationService notificationService, StringRedisTemplate redisTemplate){
        this.commRepo=commRepo;
        this.postRepo=postRepo;
        this.userRepo=userRepo;
        this.botService=botService;
        this.viralityService=viralityService;
        this.redisTemplate=redisTemplate;
        this.notificationService=notificationService;
    }

    public CommentResponse createComment(CommentRequest req, Long postId){
        Comment comment=toComment(req, postId);
        Post post=postRepo.findById(postId).orElseThrow(()->new PostNotFoundException(postId));
        if(userRepo.existsById(req.getAuthorId())){
            User user=userRepo.findById(req.getAuthorId()).get();
            viralityService.increaseScore(postId, 50);
            commRepo.save(comment);
            redisTemplate.opsForValue()
                    .set("user:"+post.getAuthorId()+":notif_cooldown", "1", 15, TimeUnit.MINUTES);
            notificationService.sendNotification(user.getUsername()+" replied to your post");
        }
        else{
            if(botService.canBotReply(postId) && botService.checkCooldown(comment.getAuthorId(), comment.getPost())){
                viralityService.increaseScore(postId, 1);
                commRepo.save(comment);
                notificationService.handleNotification(post.getAuthorId(), botService.getName(comment.getAuthorId()) + " replied to your post");
            }
        }
        return toCommentResponse(comment);
    }

    private CommentResponse toCommentResponse(Comment comment){
        return new CommentResponse(comment.getId(), comment.getAuthorId(), comment.getContent());
    }

    private Comment toComment(CommentRequest req, Long postId){
        Post post=postRepo.findById(postId).orElseThrow(()->new PostNotFoundException(postId));

        Comment parent=null;
        int newDepth=1;

        if(req.getParentCommentId()!=null){
            parent=commRepo.findById(req.getParentCommentId()).orElseThrow(()->new RuntimeException("Comment parent not found"));
        }
        else{
            return Comment.builder().authorId(req.getAuthorId()).content(req.getContent()).post(post).depthLevel(newDepth).build();
        }
        
        if(!parent.getPost().getId().equals(postId)){
            throw new RuntimeException("Invalid parent comment for this post");
        }

        String parentKey="comment:"+parent.getId()+":depth";
        String depthStr=redisTemplate.opsForValue().get(parentKey);

        int parentDepth;

        if(depthStr!=null){
            parentDepth=Integer.parseInt(depthStr);
        }
        else{
            parentDepth=parent.getDepthLevel();
        }

        newDepth=parentDepth+1;

        if(newDepth>20){
            throw new CommentDepthLimitReachedException();
        }

        return Comment.builder().authorId(req.getAuthorId()).content(req.getContent()).post(post).parentComment(parent).depthLevel(newDepth).build();

    }

}

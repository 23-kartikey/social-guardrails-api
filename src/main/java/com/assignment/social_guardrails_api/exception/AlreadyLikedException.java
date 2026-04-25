package com.assignment.social_guardrails_api.exception;

public class AlreadyLikedException extends RuntimeException{
    
    public AlreadyLikedException(Long postId, long userId){
        super("Post with ID "+postId+" already liked by user with id "+userId);
    }

}

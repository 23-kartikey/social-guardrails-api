package com.assignment.social_guardrails_api.exception;

public class PostNotFoundException extends RuntimeException {
    
    public PostNotFoundException(Long postId){
        super("Post with ID: "+postId+" not found");
    }

}

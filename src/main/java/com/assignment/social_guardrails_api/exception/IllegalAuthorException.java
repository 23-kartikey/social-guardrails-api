package com.assignment.social_guardrails_api.exception;

public class IllegalAuthorException extends RuntimeException{

    public IllegalAuthorException(String msg){
        super(msg);
    }

    public IllegalAuthorException(){
        super();
    }
    
}

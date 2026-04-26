package com.assignment.social_guardrails_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<String> handleAuthorNotFoundException(AuthorNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<String> handleAlreadyLikedException(AlreadyLikedException ex){
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    @ExceptionHandler(BotCommentLimitException.class)
    public ResponseEntity<Void> handleBotCommentLimitException(BotCommentLimitException ex){
        return ResponseEntity.status(429).build();
    }

    @ExceptionHandler(IllegalAuthorException.class)
    public ResponseEntity<String> handleIllegalAuthorException(IllegalAuthorException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}

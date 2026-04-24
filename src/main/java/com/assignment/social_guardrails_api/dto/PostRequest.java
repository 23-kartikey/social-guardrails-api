package com.assignment.social_guardrails_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    
    private Long authorId;
    private String content;

}

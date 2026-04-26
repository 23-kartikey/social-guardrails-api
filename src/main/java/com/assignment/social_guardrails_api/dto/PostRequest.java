package com.assignment.social_guardrails_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    
    @NotNull(message="AuthorId can't be empty")
    private Long authorId;
    private String content;

}

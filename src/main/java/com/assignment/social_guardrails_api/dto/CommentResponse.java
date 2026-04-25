package com.assignment.social_guardrails_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long auhtorId;
    private String content;
}

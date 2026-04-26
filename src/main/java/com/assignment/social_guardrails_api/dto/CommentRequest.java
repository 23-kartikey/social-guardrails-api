package com.assignment.social_guardrails_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {

    private String content;

    @NotNull(message="AuthorId is required")
    private Long authorId;

    private Long parentCommentId;
}

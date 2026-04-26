package com.assignment.social_guardrails_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    
    @NotBlank(message="Username can't be blank")
    private String username;
    
    private boolean isPremium;

}

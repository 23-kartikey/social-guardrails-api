package com.assignment.social_guardrails_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotRequest {
    
    @NotBlank(message="Name cannot be blank")
    private String name;

    private String personaDescription;

}

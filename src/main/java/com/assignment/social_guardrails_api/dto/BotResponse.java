package com.assignment.social_guardrails_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BotResponse {
    
    private Long id;
    private String name;
    private String personaDescription;
    
}

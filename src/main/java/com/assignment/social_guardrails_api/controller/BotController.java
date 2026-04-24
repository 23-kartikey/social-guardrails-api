package com.assignment.social_guardrails_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.social_guardrails_api.dto.BotRequest;
import com.assignment.social_guardrails_api.dto.BotResponse;
import com.assignment.social_guardrails_api.service.BotService;

@RestController
@RequestMapping("api/bots")
public class BotController {
    
    private final BotService service;

    public BotController(BotService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<BotResponse> createBot(@RequestBody BotRequest bot){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBot(bot));
    }

}

package com.assignment.social_guardrails_api.service;

import org.springframework.stereotype.Service;

import com.assignment.social_guardrails_api.dto.BotRequest;
import com.assignment.social_guardrails_api.dto.BotResponse;
import com.assignment.social_guardrails_api.entity.Bot;
import com.assignment.social_guardrails_api.repository.BotRepository;

@Service
public class BotService {
    
    private BotRepository repo;

    public BotService(BotRepository repo){
        this.repo=repo;
    }

    public BotResponse createBot(BotRequest bot){
        return toBotResponse(repo.save(toBot(bot)));
    }

    private BotResponse toBotResponse(Bot bot){
        return new BotResponse(bot.getId(), bot.getName(), bot.getPersonaDescription());
    }

    private Bot toBot(BotRequest req){
        Bot bot=new Bot();

        bot.setName(req.getName());
        bot.setPersonaDescription(req.getPersonaDescription());
        return bot;
    }

}

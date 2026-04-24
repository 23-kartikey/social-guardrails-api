package com.assignment.social_guardrails_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.social_guardrails_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}

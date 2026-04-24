package com.assignment.social_guardrails_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.social_guardrails_api.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>{
    
}

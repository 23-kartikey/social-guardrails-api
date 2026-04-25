package com.assignment.social_guardrails_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.social_guardrails_api.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long>{
    public boolean existsByPostIdAndUserId(Long postId, Long userId);
}

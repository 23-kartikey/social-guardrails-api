package com.assignment.social_guardrails_api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class User extends Author{

    @NotBlank
    @Column(nullable=false, unique=true)
    private String username;

    private boolean isPremium;


}

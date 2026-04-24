package com.assignment.social_guardrails_api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="bots")
public class Bot extends Author{

    @Column(nullable=false)
    private String name;

    @Column
    private String personaDescription;


}

package com.assignment.social_guardrails_api.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AuthorType authorType;

    @Column(nullable=false)
    private Long authorId;

    @Column(nullable=false, columnDefinition="TEXT")
    private String content;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy="post", cascade=CascadeType.ALL, orphanRemoval=true)
    List<Comment> comments;

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
    }

}

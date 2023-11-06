package com.ferroeduardo.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

@Entity
@Serdeable
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    private String  description;
    private Boolean completed;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User    user;

    public Todo() {
    }

    public Todo(Long id, String description, Boolean completed, User user) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

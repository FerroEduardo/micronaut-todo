package com.ferroeduardo.model;

import com.ferroeduardo.entity.Todo;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record TodoDTO(Long id, String description, Boolean completed) {
    public static TodoDTO fromEntity(Todo todo) {
        return new TodoDTO(todo.getId(), todo.getDescription(), todo.getCompleted());
    }
}

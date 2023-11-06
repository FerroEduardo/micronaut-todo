package com.ferroeduardo.service;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.exception.NotFoundException;
import com.ferroeduardo.repository.TodoRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Todo> index() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Todo> show(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Todo create(String description) {
        Todo todo = new Todo(null, description, Boolean.FALSE);

        return repository.save(todo);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Todo update(Long id, String description, Boolean completed) {
        Optional<Todo> optionalTodo = repository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new NotFoundException();
        }
        Todo todo = optionalTodo.get();
        todo.setDescription(description);
        if (completed != null) {
            todo.setCompleted(completed);
        }

        return repository.save(todo);
    }
}

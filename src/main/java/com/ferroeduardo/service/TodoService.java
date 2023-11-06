package com.ferroeduardo.service;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.entity.User;
import com.ferroeduardo.exception.NotFoundException;
import com.ferroeduardo.model.TodoDTO;
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
    public List<TodoDTO> index(User user) {
        return repository.findByUser(user).stream().map(TodoDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public Optional<TodoDTO> show(User user, Long id) {
        Optional<Todo> optionalTodo = repository.findByUserAndId(user, id);

        return optionalTodo.map(TodoDTO::fromEntity);
    }

    @Transactional
    public Todo create(User user, String description) {
        Todo todo = new Todo(null, description, Boolean.FALSE, user);

        return repository.save(todo);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public TodoDTO update(Long id, String description, Boolean completed) {
        Optional<Todo> optionalTodo = repository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new NotFoundException();
        }
        Todo todo = optionalTodo.get();
        todo.setDescription(description);
        if (completed != null) {
            todo.setCompleted(completed);
        }

        return TodoDTO.fromEntity(repository.save(todo));
    }
}

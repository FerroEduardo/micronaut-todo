package com.ferroeduardo.controller;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.UpdateTodo;
import com.ferroeduardo.service.TodoService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Validated
@Controller("/todo")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @Get
    public HttpResponse<List<Todo>> index() {
        List<Todo> result = service.index();

        return HttpResponse.ok(result);
    }

    @Get("{id}")
    public HttpResponse<Todo> show(@NotNull Long id) {
        Optional<Todo> todoOptional = service.show(id);
        if (todoOptional.isEmpty()) {
            return HttpResponse.notFound();
        }

        return HttpResponse.ok(todoOptional.get());
    }

    @Post
    public HttpResponse<Todo> create(@Body @Valid CreateTodo createTodo) {
        Todo todo = service.create(createTodo.getDescription());

        return HttpResponse.created(todo, URI.create("/todo/" + todo.getId()));
    }

    @Put("{id}")
    public HttpResponse<Todo> update(@NotNull Long id, @Body @Valid UpdateTodo updateTodo) {
        Todo todo = service.update(id, updateTodo.getDescription(), updateTodo.getCompleted());

        return HttpResponse.ok(todo);
    }

    @Delete("{id}")
    public HttpResponse<Object> delete(@NotNull Long id) {
        service.delete(id);

        return HttpResponse.ok();
    }
}

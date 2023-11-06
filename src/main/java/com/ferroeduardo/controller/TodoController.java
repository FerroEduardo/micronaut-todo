package com.ferroeduardo.controller;

import com.ferroeduardo.entity.User;
import com.ferroeduardo.exception.NotFoundException;
import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.TodoDTO;
import com.ferroeduardo.model.UpdateTodo;
import com.ferroeduardo.service.TodoService;
import com.ferroeduardo.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "todo")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@Controller("/todo")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @Get
    public HttpResponse<List<TodoDTO>> index(Principal principal) {
        User          user   = userService.findUserByUsername(principal.getName()).get();
        List<TodoDTO> result = todoService.index(user);

        return HttpResponse.ok(result);
    }

    @Get("{id}")
    public HttpResponse<TodoDTO> show(Principal principal, @NotNull @PositiveOrZero Long id) {
        User              user         = userService.findUserByUsername(principal.getName()).get();
        Optional<TodoDTO> todoOptional = todoService.show(user, id);
        if (todoOptional.isEmpty()) {
            return HttpResponse.notFound();
        }

        return HttpResponse.ok(todoOptional.get());
    }

    @Post
    public HttpResponse<TodoDTO> create(Principal principal, @Body @Valid CreateTodo createTodo) {
        User    user = userService.findUserByUsername(principal.getName()).get();
        TodoDTO todo = TodoDTO.fromEntity(todoService.create(user, createTodo.getDescription()));

        return HttpResponse.created(todo, URI.create("/todo/" + todo.id()));
    }

    @Post("{id}/complete/{value}")
    public HttpResponse<TodoDTO> complete(Principal principal, @NotNull @PositiveOrZero Long id, @NotNull Boolean value) {
        User user = userService.findUserByUsername(principal.getName()).get();
        todoService.complete(user, id, value);

        return HttpResponse.ok();
    }

    @Put("{id}")
    public HttpResponse<TodoDTO> update(Principal principal, @NotNull @PositiveOrZero Long id, @Body @Valid UpdateTodo updateTodo) {
        User    user = userService.findUserByUsername(principal.getName()).get();
        TodoDTO todo = todoService.update(user, id, updateTodo.getDescription(), updateTodo.getCompleted());

        return HttpResponse.ok(todo);
    }

    @Delete("{id}")
    public HttpResponse<Object> delete(Principal principal, @NotNull @PositiveOrZero Long id) {
        User user = userService.findUserByUsername(principal.getName()).get();
        todoService.delete(user, id);

        return HttpResponse.ok();
    }

    @Error(exception = NotFoundException.class)
    public HttpResponse<Map<String, Object>> todoNotFound() {
        return HttpResponse.notFound();
    }
}

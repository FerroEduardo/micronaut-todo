package com.ferroeduardo.todo;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.UpdateTodo;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Client("/todo")
public interface TodoClient {
    @Get
    HttpResponse<List<Todo>> index();

    @Get("{id}")
    HttpResponse<Todo> show(@NotNull Long id);

    @Post
    HttpResponse<Todo> create(@Body @Valid CreateTodo createTodo);

    @Put("{id}")
    HttpResponse<Todo> update(@NotNull Long id, @Body @Valid UpdateTodo updateTodo);

    @Delete("{id}")
    HttpResponse<Object> delete(@NotNull Long id);
}

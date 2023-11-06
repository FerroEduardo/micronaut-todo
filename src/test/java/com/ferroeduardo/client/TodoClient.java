package com.ferroeduardo.client;

import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.TodoDTO;
import com.ferroeduardo.model.UpdateTodo;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;

import java.util.List;

@Client("/todo")
public interface TodoClient {
    @Get
    HttpResponse<List<TodoDTO>> index(@Header String authorization);

    @Get("{id}")
    HttpResponse<TodoDTO> show(@Header String authorization, Long id);

    @Post
    HttpResponse<TodoDTO> create(@Header String authorization, @Body CreateTodo createTodo);

    @Post("{id}/complete/{value}")
    HttpResponse<TodoDTO> complete(@Header String authorization, Long id, Boolean value);

    @Put("{id}")
    HttpResponse<TodoDTO> update(@Header String authorization, Long id, @Body UpdateTodo updateTodo);

    @Delete("{id}")
    HttpResponse<Object> delete(@Header String authorization, Long id);
}

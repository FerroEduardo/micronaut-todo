package com.ferroeduardo;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.UpdateTodo;
import com.ferroeduardo.repository.TodoRepository;
import com.ferroeduardo.todo.TodoClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class TodoTest {

    @Inject
    TodoRepository repository;

    @Inject
    @Client("/todo")
    TodoClient client;

    @BeforeEach
    void beforeEach() {
        repository.save(new Todo(null, "aaaaa", Boolean.FALSE));
        repository.save(new Todo(null, "bbbbb", Boolean.FALSE));
        repository.save(new Todo(null, "ccccc", Boolean.FALSE));
    }

    @AfterEach
    void afterEach() {
        repository.deleteAll();
    }

    @Test
    void index() {
        HttpResponse<List<Todo>> response = client.index();
        assertSame(response.status(), HttpStatus.OK);
        assertEquals(3, response.body().size());
    }

    @Test
    void create() {
        HttpResponse<List<Todo>> indexResponse = client.index();
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());

        CreateTodo createTodo = new CreateTodo();
        createTodo.setDescription("opa");
        HttpResponse<Todo> createResponse = client.create(createTodo);
        Todo               todo           = createResponse.body();
        assertSame(HttpStatus.CREATED, createResponse.status());
        assertEquals("opa", todo.getDescription());
        assertNotNull(todo.getId());
        assertFalse(todo.getCompleted());

        indexResponse = client.index();
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(4, indexResponse.body().size());
    }

    @Test
    void delete() {
        HttpResponse<List<Todo>> indexResponse = client.index();
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        Todo todo = indexResponse.body().get(0);

        HttpResponse<Object> deleteRequest = client.delete(todo.getId());
        assertSame(HttpStatus.OK, deleteRequest.status());

        indexResponse = client.index();
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(2, indexResponse.body().size());
    }

    @Test
    void update() {
        HttpResponse<List<Todo>> indexResponse = client.index();
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        Todo todo = indexResponse.body().get(0);

        UpdateTodo updateTodo = new UpdateTodo();
        updateTodo.setDescription("zzzzz");
        updateTodo.setCompleted(true);
        HttpResponse<Todo> updateRequest = client.update(todo.getId(), updateTodo);
        assertSame(HttpStatus.OK, updateRequest.status());

        HttpResponse<Todo> showRequest = client.show(todo.getId());
        assertSame(showRequest.status(), HttpStatus.OK);
        assertEquals("zzzzz", showRequest.body().getDescription());
        assertEquals(true, showRequest.body().getCompleted());
    }

}

package com.ferroeduardo;

import com.ferroeduardo.client.TodoClient;
import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.entity.User;
import com.ferroeduardo.model.CreateTodo;
import com.ferroeduardo.model.TodoDTO;
import com.ferroeduardo.model.UpdateTodo;
import com.ferroeduardo.repository.TodoRepository;
import com.ferroeduardo.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class TodoTest {

    @Inject
    TodoRepository todoRepository;
    @Inject
    UserRepository userRepository;

    @Inject
    @Client("/todo")
    TodoClient todoClient;

    @Inject
    @Client("/")
    HttpClient client;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(new User("eduardo", "$2a$10$EyAJoC0p6ewPnZcQMg5gbuE1q/dtl3kWCCxOq9EGOfRxO53roLS/G"));
        todoRepository.save(new Todo(null, "aaaaa", Boolean.FALSE, user));
        todoRepository.save(new Todo(null, "bbbbb", Boolean.FALSE, user));
        todoRepository.save(new Todo(null, "ccccc", Boolean.FALSE, user));
    }

    @AfterEach
    void afterEach() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String getAuthenticationToken() {
        Map response = client
                .toBlocking()
                .exchange(
                        HttpRequest.POST(
                                        "/auth/signin",
                                        """
                                                {
                                                    "username": "eduardo",
                                                    "password": "senha"
                                                }
                                                """
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON),
                        Map.class
                ).body();

        return "Bearer " + response.get("access_token").toString();
    }

    @Test
    void indexUnauthenticated() {
        HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
            HttpResponse<List<TodoDTO>> response = todoClient.index("");
        });
        assertEquals("Unauthorized", httpClientResponseException.getMessage());
    }

    @Test
    void index() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> response            = todoClient.index(authenticationToken);
        assertSame(response.status(), HttpStatus.OK);
        assertEquals(3, response.body().size());
    }

    @Test
    void create() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> indexResponse       = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());

        CreateTodo createTodo = new CreateTodo();
        createTodo.setDescription("opa");
        HttpResponse<TodoDTO> createResponse = todoClient.create(authenticationToken, createTodo);
        TodoDTO               todo           = createResponse.body();
        assertSame(HttpStatus.CREATED, createResponse.status());
        assertEquals("opa", todo.description());
        assertNotNull(todo.id());
        assertFalse(todo.completed());

        indexResponse = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(4, indexResponse.body().size());
    }

    @Test
    void delete() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> indexResponse       = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        TodoDTO todo = indexResponse.body().get(0);

        HttpResponse<Object> deleteRequest = todoClient.delete(authenticationToken, todo.id());
        assertSame(HttpStatus.OK, deleteRequest.status());

        indexResponse = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(2, indexResponse.body().size());
    }

    @Test
    void update() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> indexResponse       = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        TodoDTO todo = indexResponse.body().get(0);

        UpdateTodo updateTodo = new UpdateTodo();
        updateTodo.setDescription("zzzzz");
        updateTodo.setCompleted(true);
        HttpResponse<TodoDTO> updateRequest = todoClient.update(authenticationToken, todo.id(), updateTodo);
        assertSame(HttpStatus.OK, updateRequest.status());

        HttpResponse<TodoDTO> showRequest = todoClient.show(authenticationToken, todo.id());
        assertSame(showRequest.status(), HttpStatus.OK);
        assertEquals("zzzzz", showRequest.body().description());
        assertEquals(true, showRequest.body().completed());
    }

    @Test
    void invalidUpdate() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> indexResponse       = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        long maxId = indexResponse.body().stream().mapToLong(TodoDTO::id).max().getAsLong();

        UpdateTodo updateTodo = new UpdateTodo();
        updateTodo.setDescription("zzzzz");
        updateTodo.setCompleted(true);
        HttpResponse<TodoDTO> updateRequest = todoClient.update(authenticationToken, maxId + 1, updateTodo);
        assertSame(HttpStatus.NOT_FOUND, updateRequest.status());
    }

    @Test
    void complete() {
        String                      authenticationToken = getAuthenticationToken();
        HttpResponse<List<TodoDTO>> indexResponse       = todoClient.index(authenticationToken);
        assertSame(indexResponse.status(), HttpStatus.OK);
        assertEquals(3, indexResponse.body().size());
        TodoDTO todo = indexResponse.body().get(0);
        assertEquals(false, todo.completed());

        HttpResponse<TodoDTO> updateRequest = todoClient.complete(authenticationToken, todo.id(), true);
        assertSame(HttpStatus.OK, updateRequest.status());

        HttpResponse<TodoDTO> showRequest = todoClient.show(authenticationToken, todo.id());
        assertSame(showRequest.status(), HttpStatus.OK);
        assertEquals(true, showRequest.body().completed());
    }
}

package com.ferroeduardo;

import com.ferroeduardo.entity.User;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AuthenticationTest {

    @Inject
    UserRepository userRepository;

    @Inject
    @Client("/")
    HttpClient client;

    @BeforeEach
    void beforeEach() {
        userRepository.save(new User("eduardo", "$2a$10$EyAJoC0p6ewPnZcQMg5gbuE1q/dtl3kWCCxOq9EGOfRxO53roLS/G"));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void signin() {
        HttpResponse<Map> response = client
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
                );

        assertSame(response.status(), HttpStatus.OK);
    }

    @Test
    void signinInvalidCredentials() {
        HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
            client
                    .toBlocking()
                    .exchange(
                            HttpRequest.POST(
                                            "/auth/signin",
                                            """
                                                    {
                                                        "username": "eduardo",
                                                        "password": "password"
                                                    }
                                                    """
                                    )
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON),
                            Map.class
                    );
        });
        assertEquals("Credentials Do Not Match", httpClientResponseException.getMessage());
    }

    @Test
    void signup() {
        HttpResponse<Map> response = client
                .toBlocking()
                .exchange(
                        HttpRequest.POST(
                                        "/auth/signup",
                                        """
                                                {
                                                    "username": "ferro",
                                                    "password": "senha"
                                                }
                                                """
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON),
                        Map.class
                );

        assertSame(response.status(), HttpStatus.OK);
    }

    @Test
    void signupUsernameAlreadyTaken() {
        HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
            client
                    .toBlocking()
                    .exchange(
                            HttpRequest.POST(
                                            "/auth/signup",
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
                    );
        });
        assertEquals("Username already taken", httpClientResponseException.getMessage());
    }
}

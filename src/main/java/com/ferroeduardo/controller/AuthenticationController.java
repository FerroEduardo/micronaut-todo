package com.ferroeduardo.controller;

import com.ferroeduardo.exception.UsernameAlreadyTaken;
import com.ferroeduardo.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/auth")
@Tag(name = "auth")
public class AuthenticationController {

    private final UserService service;

    public AuthenticationController(UserService service) {
        this.service = service;
    }

    @Post("/signup")
    public HttpResponse<Object> signup(@NotNull @NotBlank String username, @NotNull @NotBlank String password) {
        service.signup(username, password);

        return HttpResponse.ok();
    }

    @Error(exception = UsernameAlreadyTaken.class)
    public HttpResponse<Map<String, String>> usernameAlreadyTaken() {
        Map<String, String> model = Map.of("message", "Username already taken");

        return HttpResponse.notFound(model);
    }
}

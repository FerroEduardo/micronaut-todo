package com.ferroeduardo.authentication;

import com.ferroeduardo.entity.User;
import com.ferroeduardo.service.UserService;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Singleton
public class UserAuthenticationProvider implements AuthenticationProvider<HttpRequest<?>> {

    private final UserService     service;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationProvider(UserService service, PasswordEncoderService passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        String identity = authenticationRequest.getIdentity().toString();
        String secret   = authenticationRequest.getSecret().toString();

        Optional<User> userOptional = service.findUserByUsername(identity);
        if (userOptional.isEmpty()) {
            return Publishers.just((AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)));
        }

        User user = userOptional.get();
        if (!this.passwordEncoder.matches(secret, user.getPassword())) {
            return Publishers.just((AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)));
        }

        return Publishers.just(AuthenticationResponse.success(identity, List.of("USER")));
    }
}

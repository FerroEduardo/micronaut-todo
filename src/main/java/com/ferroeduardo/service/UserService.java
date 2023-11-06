package com.ferroeduardo.service;

import com.ferroeduardo.entity.User;
import com.ferroeduardo.exception.UsernameAlreadyTaken;
import com.ferroeduardo.repository.UserRepository;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Singleton
public class UserService {

    private final UserRepository  repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public void signup(String username, String password) {
        Optional<User> optionalUser = this.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyTaken();
        }

        password = passwordEncoder.encode(password);
        User user = new User(username, password);
        repository.save(user);
    }
}

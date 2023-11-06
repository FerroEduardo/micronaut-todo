package com.ferroeduardo.repository;

import com.ferroeduardo.entity.Todo;
import com.ferroeduardo.entity.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(User user);
    Optional<Todo> findByUserAndId(User user, Long id);
}

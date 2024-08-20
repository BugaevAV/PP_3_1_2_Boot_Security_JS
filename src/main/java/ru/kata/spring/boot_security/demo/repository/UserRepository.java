package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select distinct u from User u left join fetch u.roles where u.username = :username")
    Optional<User> findByUsername(String username);
}

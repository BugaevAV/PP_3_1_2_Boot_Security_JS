package ru.kata.spring.boot_security.demo.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserRolesDTO;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @SneakyThrows
    @GetMapping
    public List<User> adminPage() {
        return userService.findAll();
    }

    @Transactional
    @PostMapping("/save_user")
    public void saveNewUser(@RequestBody UserRolesDTO request) {
        userService.addUser(request);
    }

    @Transactional
    @PatchMapping("/update_user")
    public void updateUser(@RequestBody UserRolesDTO request) {
        Optional<User> currentUserState = userService.findById(request.getId());
        if (currentUserState.isPresent()) {
            User user = currentUserState.get();
            if (!user.getUsername().equals(request.getUsername())) {
                user.setUsername(request.getUsername());
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                user.setPassword(request.getPassword());
            }
            if (!user.getEmail().equals(request.getEmail())) {
                user.setEmail(request.getEmail());
            }
            user.setRoles(request.getRoleNames().stream()
                    .map(roleService::getRoleByName)
                    .collect(Collectors.toList()));
            userService.updateUser(user);
        }
    }

    @Transactional
    @DeleteMapping("/delete_user/{id}")
    public void deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        user.ifPresent(userService::removeUser);
    }

}

package ru.kata.spring.boot_security.demo.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserRolesDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String getAdminPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "admin";
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/get_all_users")
    public List<User> adminPage() {
        return userService.findAll();
    }


    @SneakyThrows
    @ResponseBody
    @GetMapping("/get_roles_exist")
    public List<String> rolesExits() {
        return roleService.getAllRoles().stream()
                .map(Role::getName).collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping("/get_user/{id}")
    public User getUser(@PathVariable long id) {
        return userService.findById(id).orElseThrow(RuntimeException::new);
    }

    @ResponseBody
    @GetMapping("/personal_info")
    public User getAdminInfo(Model model, HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @Transactional
    @ResponseBody
    @PostMapping("/add_new_user")
    public void saveNewUser(@RequestBody UserRolesDTO request) {
        userService.addUser(request);
    }

    @Transactional
    @ResponseBody
    @PatchMapping("/update_user/{id}")
    public void updateUser(@RequestBody UserRolesDTO request, @PathVariable long id) {
        Optional<User> currentUserState = userService.findById(id);
        if (currentUserState.isPresent()) {
            User user = currentUserState.get();
            if (!user.getUsername().equals(request.getUsername())) {
                user.setUsername(request.getUsername());
            }
            if (!request.getPassword().equals(user.getPassword())) {
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
    @ResponseBody
    @DeleteMapping("/delete_user/{id}")
    public void deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        user.ifPresent(userService::removeUser);
    }

}

package ru.kata.spring.boot_security.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public String adminPage(ModelMap model, HttpSession session) {
        User thisUser = (User)session.getAttribute("user");
        model.addAttribute("allUsers", userService.findAll());
        model.addAttribute("thisUser", thisUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin";
    }

    @Transactional
    @PostMapping("/save_user")
    public String saveNewUser(@ModelAttribute("user") User user,
                              @RequestParam(value = "role_names", required = false) List<String> roleNames) {
        User newUser = userService.addUser(user);
        if (roleNames != null) {
            List<Role> roles = roleNames.stream()
                    .map(roleService::getRoleByName)
                    .collect(Collectors.toList());
            newUser.setRoles(roles);
        }
        return "redirect:/api/v1/admin";
    }

    @PatchMapping("/update_user")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/api/v1/admin";
    }

    @GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        user.ifPresent(userService::removeUser);
        return "redirect:/api/v1/admin";
    }

}

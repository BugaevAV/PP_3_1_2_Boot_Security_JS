package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;


import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MyController {

    private final RoleService roleService;

    public MyController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(ModelMap model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        if (new HashSet<>(user.getRoleNames()).containsAll(roleService.getAllRoles()
                .stream().map(Role::getName).collect(Collectors.toList()))) {
            return "admin";
        } else {
            return "user";
        }
    }
}

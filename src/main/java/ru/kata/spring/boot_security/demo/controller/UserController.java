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

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/")
    public String index(ModelMap model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/save_user")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/login";
    }

    @GetMapping("/user_page")
    public String userPage(HttpSession session, ModelMap model) {
        User user = (User)session.getAttribute("user");
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage(ModelMap model) {
        List<User> users = userService.findAll();
        model.addAttribute("allUsers", users);
        return "admin";
    }

    @GetMapping("/admin/new_user")
    public String newUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        return "user_form";
    }

    @Transactional
    @PostMapping("/admin/save_user")
    public String saveNewUser(@ModelAttribute("user") User user, @RequestParam("role_name") String roleName) {
        User newUser = userService.addUser(user);
        Role role = roleService.getRoleByName(roleName);
        newUser.getRoles().add(role);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete_user/{id}")
    public String deleteUser(@PathVariable int id) {
        Optional<User> user = userService.findById((long) id);
        user.ifPresent(userService::removeUser);
        return "redirect:/admin";
    }

}

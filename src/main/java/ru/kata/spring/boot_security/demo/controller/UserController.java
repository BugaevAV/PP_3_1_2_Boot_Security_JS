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
        return "new_user_form";
    }

    @Transactional
    @PostMapping("/admin/save_user")
    public String saveNewUser(@ModelAttribute("user") User user,
                              @RequestParam("role_names") List<String> roleNames) {
        User newUser = userService.addUser(user);
        List<Role> roles = roleNames.stream()
                .map(roleService::getRoleByName)
                .collect(Collectors.toList());
        newUser.setRoles(roles);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update_user/{id}")
    public String updateUserForm(@PathVariable Long id, ModelMap model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("chosen_roles", user.get().getRoles());
            return "update_user_form";
        }
        return "redirect:/admin";
    }

    @Transactional
    @PostMapping("/admin/update_user/{id}")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam("role_names") List<String> roleNames) {
        User updatedUser = userService.addUser(user);
        List<Role> roles = roleNames.stream()
                .map(roleService::getRoleByName)
                .collect(Collectors.toList());
        updatedUser.setRoles(roles);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete_user/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        user.ifPresent(userService::removeUser);
        return "redirect:/admin";
    }

}

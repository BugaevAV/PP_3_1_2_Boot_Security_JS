package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;


import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/v1/")
public class UserController {

    @GetMapping
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String userPage(HttpSession session, ModelMap model) {
        User user = (User)session.getAttribute("user");
        model.addAttribute("user", user);
        return "user";
    }

    @ResponseBody
    @GetMapping("/user_information")
    public User getUserInformation(HttpSession session) {
        return (User) session.getAttribute("user");
    }

}

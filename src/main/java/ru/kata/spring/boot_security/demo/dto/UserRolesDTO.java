package ru.kata.spring.boot_security.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRolesDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private List<String> roleNames;
}

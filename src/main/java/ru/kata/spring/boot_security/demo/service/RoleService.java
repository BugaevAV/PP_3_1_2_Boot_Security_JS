package ru.kata.spring.boot_security.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepo;

    public void saveRole(Role role) {
        roleRepo.save(role);
    }

    public Role getRoleByName(String roleName) {
        return roleRepo.findAll().stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst().orElse(null);
    }

    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }
}

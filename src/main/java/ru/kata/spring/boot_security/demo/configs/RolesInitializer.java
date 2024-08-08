package ru.kata.spring.boot_security.demo.configs;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

@Component
@AllArgsConstructor
public class RolesInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findAll().stream()
                .noneMatch(role -> role.getName().equals("ROLE_ADMIN")
                        || role.getName().equals("ROLE_USER"))) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_USER"));
        }
    }
}

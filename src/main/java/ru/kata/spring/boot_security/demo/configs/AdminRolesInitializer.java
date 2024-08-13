package ru.kata.spring.boot_security.demo.configs;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class AdminRolesInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findAll().stream()
                .noneMatch(role -> role.getName().equals("ROLE_ADMIN")
                        || role.getName().equals("ROLE_USER"))) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_USER"));
        }

        if (userRepository.findAll().stream()
                .noneMatch(user -> user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN")))) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("admin@admin.com");
            user.setRoles(new ArrayList<>(roleRepository.findAll()));
            userRepository.save(user);
        }

    }
}

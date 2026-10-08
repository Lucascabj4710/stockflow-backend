package com.stockflow_backend.configuration;

import com.stockflow_backend.entities.Role;
import com.stockflow_backend.entities.UserEntity;
import com.stockflow_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Value("${user.admin.username}")
    private String username;
    @Value("${user.admin.password}")
    private String password;

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (!userRepository.existsByUsername(username)) {

                UserEntity admin = new UserEntity();

                admin.setUsername(username);
                admin.setPassword(
                        passwordEncoder.encode(password)
                );

                admin.setEnabled(true);
                admin.setAccountNonExpired(true);
                admin.setAccountNonLocked(true);
                admin.setCredentialsNonExpired(true);

                admin.setRoleSet(
                        Set.of(Role.ADMIN)
                );

                userRepository.save(admin);
            }
        };
    }
}

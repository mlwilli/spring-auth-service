package com.github.mlwilli.authservice;

import com.github.mlwilli.authservice.domain.Role;
import com.github.mlwilli.authservice.domain.User;
import com.github.mlwilli.authservice.repository.RoleRepository;
import com.github.mlwilli.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initData(UserRepository users,
                               RoleRepository roles,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.count() > 0) {
                return;
            }

            Role adminRole = roles.findByName("ROLE_ADMIN").orElseGet(() -> roles.save(new Role("ROLE_ADMIN")));
            Role userRole = roles.findByName("ROLE_USER").orElseGet(() -> roles.save(new Role("ROLE_USER")));

            User admin = new User()
                    .setUsername("admin")
                    .setPasswordHash(passwordEncoder.encode("admin123"))
                    .setEnabled(true)
                    .setRoles(Set.of(adminRole, userRole));

            User user = new User()
                    .setUsername("user")
                    .setPasswordHash(passwordEncoder.encode("user123"))
                    .setEnabled(true)
                    .setRoles(Set.of(userRole));

            users.save(admin);
            users.save(user);

            log.info("Created demo users: admin/admin123, user/user123");
        };
    }
}

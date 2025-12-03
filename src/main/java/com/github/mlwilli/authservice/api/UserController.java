package com.github.mlwilli.authservice.api;

import com.github.mlwilli.authservice.api.dto.UserRequest;
import com.github.mlwilli.authservice.api.dto.UserResponse;
import com.github.mlwilli.authservice.domain.Role;
import com.github.mlwilli.authservice.domain.User;
import com.github.mlwilli.authservice.repository.RoleRepository;
import com.github.mlwilli.authservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository users,
                          RoleRepository roles,
                          PasswordEncoder passwordEncoder) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return users.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toResponse(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        if (users.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(resolveRoles(request.getRoles()));

        User saved = users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UserRequest request) {
        User existing = users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        existing.setUsername(request.getUsername());
        existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        existing.setRoles(resolveRoles(request.getRoles()));

        User updated = users.save(existing);
        return toResponse(updated);
    }

    @PatchMapping("/{id}/enabled")
    public UserResponse updateEnabled(@PathVariable Long id,
                                      @RequestParam boolean enabled) {
        User existing = users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        existing.setEnabled(enabled);
        return toResponse(users.save(existing));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        users.deleteById(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    private UserResponse toResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEnabled(user.isEnabled())
                .setRoles(roleNames);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of();
        }
        Set<Role> result = new HashSet<>();
        for (String name : roleNames) {
            Role role = roles.findByName(name)
                    .orElseGet(() -> roles.save(new Role(name)));
            result.add(role);
        }
        return result;
    }
}

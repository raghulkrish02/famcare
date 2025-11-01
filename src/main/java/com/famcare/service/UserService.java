package com.famcare.service;

import com.famcare.model.User;
import com.famcare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    public User register(String username, String rawPassword, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
        u.setRole(role);
        return userRepository.save(u);
    }

    public Long userIdFor(String username) {
        return userRepository.findByUsername(username).map(User::getId).orElse(null);
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }
}

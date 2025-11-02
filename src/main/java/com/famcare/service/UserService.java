package com.famcare.service;

import com.famcare.model.User;
import com.famcare.repository.FamilyMemberRepository; // IMPORT THIS
import com.famcare.repository.MoodEntryRepository;  // IMPORT THIS
import com.famcare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // IMPORT THIS

import java.util.List; // IMPORT THIS
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // --- ADD THESE REPOSITORIES ---
    private final FamilyMemberRepository familyMemberRepository;
    private final MoodEntryRepository moodEntryRepository;

    // --- UPDATE THE CONSTRUCTOR ---
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       FamilyMemberRepository familyMemberRepository,
                       MoodEntryRepository moodEntryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.familyMemberRepository = familyMemberRepository;
        this.moodEntryRepository = moodEntryRepository;
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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // --- ADD THIS ENTIRE METHOD ---
    @Transactional // This makes the whole method one database operation
    public void deleteUser(Long userId) {
        // 1. Delete all family memberships for this user
        familyMemberRepository.deleteByUserId(userId);

        // 2. Delete all journal entries for this user
        moodEntryRepository.deleteByUserId(userId);

        // 3. Finally, delete the user itself
        userRepository.deleteById(userId);
    }
}
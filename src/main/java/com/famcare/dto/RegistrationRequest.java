package com.famcare.dto;

// This class is a Data Transfer Object (DTO).
// It's used to pass the data from the registration form
// to the AuthController.

public class RegistrationRequest {
    private String username;
    private String password;
    private Long familyId;
    private String newFamilyName;

    // --- Getters and Setters ---
    // These are necessary for Spring to read the values.

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getNewFamilyName() {
        return newFamilyName;
    }

    public void setNewFamilyName(String newFamilyName) {
        this.newFamilyName = newFamilyName;
    }
}

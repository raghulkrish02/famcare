package com.famcare.controller;

import com.famcare.dto.RegistrationRequest; // 1. Import the DTO
import com.famcare.model.Family;
import com.famcare.model.FamilyMember;
import com.famcare.model.User;
import com.famcare.service.FamilyService;
import com.famcare.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final FamilyService familyService;

    // Inject the required services
    public AuthController(UserService userService, FamilyService familyService) {
        this.userService = userService;
        this.familyService = familyService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // UPDATED: Show registration page
    @GetMapping("/register")
    public String register(Model model) {
        // Add the empty request object for the form
        model.addAttribute("request", new RegistrationRequest());
        // Add the list of all families for the dropdown
        model.addAttribute("families", familyService.allFamilies());
        return "register";
    }

    // UPDATED: Handle the new registration logic
    @PostMapping("/register")
    public String register(@ModelAttribute("request") RegistrationRequest request) {

        Long familyIdToJoin;

        // 1. Check if user is creating a new family
        if (request.getNewFamilyName() != null && !request.getNewFamilyName().isEmpty()) {
            // Create and save the new family
            Family newFamily = new Family();
            newFamily.setFamilyName(request.getNewFamilyName());
            Family savedFamily = familyService.saveFamily(newFamily);
            familyIdToJoin = savedFamily.getId();
        } else {
            // Use the family ID from the dropdown
            familyIdToJoin = request.getFamilyId();
        }

        // 2. Create the new user
        // FIX: Call your existing 'register' method with a default role
        User newUser = userService.register(
                request.getUsername(),
                request.getPassword(),
                "USER" // Default role for new signups
        );

        // 3. Link the user to the family
        FamilyMember fm = new FamilyMember();
        fm.setUserId(newUser.getId());
        fm.setFamilyId(familyIdToJoin);
        familyService.addMember(fm);

        // 4. Redirect to login
        return "redirect:/login?registered";
    }

}


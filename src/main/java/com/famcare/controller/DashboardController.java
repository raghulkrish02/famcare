package com.famcare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth != null ? auth.getName() : "Guest");
        return "dashboard";
    }
    // Inside a controller, e.g., DashboardController.java
    @GetMapping("/remedies")
    public String showRemedies() {
        return "remedies"; // Renders remedies.html
    }
}


package com.famcare.controller;

import com.famcare.model.Family;
import com.famcare.model.FamilyMember;
import com.famcare.service.FamilyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/family")
@PreAuthorize("hasRole('ADMIN')")
public class FamilyController {
    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) { this.familyService = familyService; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("families", familyService.allFamilies());
        model.addAttribute("family", new Family());
        return "family-dashboard";
    }

    @PostMapping
    public String create(@ModelAttribute Family family) {
        familyService.saveFamily(family);
        return "redirect:/admin/family";
    }

    @PostMapping("/{id}/member")
    public String addMember(@PathVariable Long id, @ModelAttribute FamilyMember fm) {
        fm.setFamilyId(id);
        familyService.addMember(fm);
        return "redirect:/admin/family";
    }
}

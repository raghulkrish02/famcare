package com.famcare.controller;

import com.famcare.model.*;
import com.famcare.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- ADD THIS IMPORT
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/family")
@PreAuthorize("hasRole('ADMIN')")
public class FamilyController {
    // ... (constructor and other fields are unchanged) ...
    private final FamilyService familyService;
    private final UserService userService;
    private final MoodEntryService moodEntryService;
    private final AnalyticsService analyticsService;

    public FamilyController(FamilyService familyService, UserService userService,
                            MoodEntryService moodEntryService, AnalyticsService analyticsService) {
        this.familyService = familyService;
        this.userService = userService;
        this.moodEntryService = moodEntryService;
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public String list(Model model, Authentication auth) { // <-- ADD Authentication auth HERE
        // ... (all the existing code in this method is the same) ...
        List<Family> families = familyService.allFamilies();
        List<FamilyMember> members = familyService.allMembers();
        List<User> users = userService.findAll();

        Map<Long, String> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        Map<Long, List<FamilyMember>> membersByFamily = members.stream()
                .collect(Collectors.groupingBy(FamilyMember::getFamilyId));

        model.addAttribute("families", families);
        model.addAttribute("membersByFamily", membersByFamily);
        model.addAttribute("userMap", userMap);
        model.addAttribute("family", new Family());

        // --- THIS IS THE NEW LINE ---
        // Add the current admin's username to the model
        model.addAttribute("currentUsername", auth.getName());

        return "family-dashboard";
    }

    // ... (rest of the controller is unchanged) ...

    @PostMapping
    public String create(@ModelAttribute Family family) {
        familyService.saveFamily(family);
        return "redirect:/admin/family";
    }

    // Updated to be simpler
    @PostMapping("/{familyId}/member")
    public String addMember(@PathVariable Long familyId, @RequestParam Long userId) {
        FamilyMember fm = new FamilyMember();
        fm.setFamilyId(familyId);
        fm.setUserId(userId);
        familyService.addMember(fm);
        return "redirect:/admin/family";
    }

    // --- NEW METHOD for Family Details Page ---
    @GetMapping("/{familyId}")
    public String familyDetails(@PathVariable Long familyId, Model model) {
        // 1. Get the Family
        Family family = familyService.findFamilyById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        // 2. Get all members of this family
        List<FamilyMember> members = familyService.findMembersByFamilyId(familyId);
        List<Long> memberUserIds = members.stream().map(FamilyMember::getUserId).toList();

        // 3. Get all journal entries for these members
        List<MoodEntry> entries = moodEntryService.findByUserIdIn(memberUserIds);

        // 4. Calculate family-wide averages
        AnalyticsService.Averages avg = analyticsService.averagesForEntries(entries);
        String suggestion = analyticsService.suggestionFor(avg);

        // 5. Get user data for display
        Map<Long, String> userMap = userService.findAll().stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        model.addAttribute("family", family);
        model.addAttribute("members", members);
        model.addAttribute("userMap", userMap);
        model.addAttribute("entries", entries); // For journal history
        model.addAttribute("avgMood", avg.averageMood());
        model.addAttribute("avgStress", avg.averageStress());
        model.addAttribute("suggestion", suggestion);

        // Data for the graph (passed as JSON string)
        List<String> dates = entries.stream()
                .map(e -> e.getDate().toString()).distinct().sorted().toList();
        // This is a simplified graph - it averages all members per day
        Map<String, Double> avgMoodPerDay = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getDate().toString(),
                        Collectors.averagingInt(MoodEntry::getMoodLevel)));
        Map<String, Double> avgStressPerDay = entries.stream()
                .collect(Collectors.groupingBy(e -> e.getDate().toString(),
                        Collectors.averagingInt(MoodEntry::getStressLevel)));

        List<Double> moods = dates.stream().map(d -> avgMoodPerDay.getOrDefault(d, 0.0)).toList();
        List<Double> stresses = dates.stream().map(d -> avgStressPerDay.getOrDefault(d, 0.0)).toList();

        model.addAttribute("chartLabels", dates);
        model.addAttribute("chartMoods", moods);
        model.addAttribute("chartStresses", stresses);

        return "family-details"; // The new HTML file
    }

    @PostMapping("/member/delete/{memberId}")
    public String removeMember(@PathVariable Long memberId) {
        familyService.removeMember(memberId);
        return "redirect:/admin/family";
    }

    @PostMapping("/delete/{familyId}")
    public String deleteFamily(@PathVariable Long familyId) {
        familyService.deleteFamily(familyId);
        return "redirect:/admin/family";
    }

    @PostMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/family";
    }
}
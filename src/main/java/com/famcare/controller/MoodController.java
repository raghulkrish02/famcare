package com.famcare.controller;

import com.famcare.model.MoodEntry;
import com.famcare.model.User;
import com.famcare.repository.UserRepository;
import com.famcare.service.AnalyticsService;
import com.famcare.service.MoodEntryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/journal")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Assuming only users can journal
public class MoodController {

    private final MoodEntryService moodEntryService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository; // For getting real User ID

    public MoodController(MoodEntryService moodEntryService, AnalyticsService analyticsService, UserRepository userRepository) {
        this.moodEntryService = moodEntryService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    // Helper to get stable User ID
    private Long getUserId(Authentication auth) {
        if (auth == null) return null;
        Optional<User> userOptional = userRepository.findByUsername(auth.getName());
        return userOptional.map(User::getId).orElse(null);
    }

    @GetMapping
    public String showJournalForm(Model model, Authentication auth) {
        Long userId = getUserId(auth);
        if (userId == null) {
            return "redirect:/login"; // Should not happen if authenticated
        }

        // --- FIX: FETCH AND ADD HISTORY ---
        List<MoodEntry> entries = moodEntryService.findByUser(userId);
        model.addAttribute("entries", entries);

        // Add empty entry for the form
        model.addAttribute("entry", new MoodEntry());
        return "journal";
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        // Ensure the user owns this entry before deleting
        moodEntryService.findById(id)
                .ifPresent(entry -> {
                    if (entry.getUserId().equals(userId)) {
                        moodEntryService.deleteById(id);
                    }
                });
        return "redirect:/journal";
    }


    @PostMapping("/assess")
    public String submitAssessment(@RequestParam Map<String, String> allParams, Authentication auth, RedirectAttributes redirectAttributes) {
        Long userId = getUserId(auth);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            // 1. Calculate Scores
            int stressScore = calculateStressScore(allParams);
            int moodScore = calculateMoodScore(allParams);

            // 2. Generate Note
            String note = "Daily Assessment Completed.";

            // 3. Save the new MoodEntry
            MoodEntry entry = new MoodEntry();
            entry.setUserId(userId);
            entry.setDate(LocalDate.now()); // Set to current date
            entry.setMoodLevel(moodScore);
            entry.setStressLevel(stressScore);
            entry.setNotes(note);
            MoodEntry savedEntry = moodEntryService.save(entry);

            // 4. Add the ID to RedirectAttributes so /remedy can fetch it
            redirectAttributes.addFlashAttribute("latestEntryId", savedEntry.getId());
            return "redirect:/remedy";

        } catch (Exception e) {
            // Handle error (e.g., user didn't answer all questions)
            redirectAttributes.addFlashAttribute("error", "Please answer all questions before submitting.");
            return "redirect:/journal";
        }
    }

    // --- Helper Methods for Assessment ---

    // Stress = Q1 (Tense) + Q6 (Overwhelmed) + Q12 (Worrying) + Q14 (Appetite) + Q15 (Irritable)
    // Max Score = 3 + 3 + 3 + 1 + 3 = 13
    private int calculateStressScore(Map<String, String> params) {
        int q1 = Integer.parseInt(params.get("q1"));
        int q6 = Integer.parseInt(params.get("q6"));
        int q12 = Integer.parseInt(params.get("q12"));
        int q14 = Integer.parseInt(params.get("q14"));
        int q15 = Integer.parseInt(params.get("q15"));

        // Total score is 0-13. Scale it to 1-10.
        double total = q1 + q6 + q12 + q14 + q15;
        // (total / 13) * 9 + 1 = Scaled score from 1 (low stress) to 10 (high stress)
        int scaledScore = (int) Math.round((total / 13.0) * 9.0) + 1;
        return Math.max(1, Math.min(10, scaledScore)); // Clamp between 1 and 10
    }

    // Mood = Q2, Q3, Q4, Q5, Q7, Q8, Q9, Q10, Q11, Q13
    // Max Score = 3 * 10 = 30
    private int calculateMoodScore(Map<String, String> params) {
        int q2 = Integer.parseInt(params.get("q2"));
        int q3 = Integer.parseInt(params.get("q3"));
        int q4 = Integer.parseInt(params.get("q4"));
        int q5 = Integer.parseInt(params.get("q5"));
        int q7 = Integer.parseInt(params.get("q7"));
        int q8 = Integer.parseInt(params.get("q8"));
        int q9 = Integer.parseInt(params.get("q9"));
        int q10 = Integer.parseInt(params.get("q10"));
        int q11 = Integer.parseInt(params.get("q11"));
        int q13 = Integer.parseInt(params.get("q13"));

        // Total score is 0-30. Scale it to 1-10.
        double total = q2 + q3 + q4 + q5 + q7 + q8 + q9 + q10 + q11 + q13;
        // (total / 30) * 9 + 1 = Scaled score from 1 (low mood) to 10 (high mood)
        int scaledScore = (int) Math.round((total / 30.0) * 9.0) + 1;
        return Math.max(1, Math.min(10, scaledScore)); // Clamp between 1 and 10
    }

}


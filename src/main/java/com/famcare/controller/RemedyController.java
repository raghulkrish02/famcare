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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class RemedyController {

    private final MoodEntryService moodEntryService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public RemedyController(MoodEntryService moodEntryService, AnalyticsService analyticsService, UserRepository userRepository) {
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

    /**
     * This method handles showing the remedy right after a user
     * submits a new assessment.
     */
    @GetMapping("/remedy")
    public String showRemedyForLatest(@ModelAttribute("latestEntryId") Long latestEntryId, Model model, Authentication auth) {
        Long userId = getUserId(auth);
        if (userId == null) return "redirect:/login";

        Optional<MoodEntry> entryOptional;

        if (latestEntryId != null) {
            entryOptional = moodEntryService.findById(latestEntryId);
            if (entryOptional.isPresent() && !entryOptional.get().getUserId().equals(userId)) {
                return "redirect:/access-denied";
            }
        } else {
            List<MoodEntry> recent = moodEntryService.findRecentByUser(userId);
            entryOptional = recent.stream().findFirst();
        }

        return prepareRemedyModel(entryOptional, model);
    }

    /**
     * This method handles showing the remedy for any specific entry
     * when clicked from the history table.
     */
    @GetMapping("/remedy/{id}")
    public String showRemedyForId(@PathVariable("id") Long id, Model model, Authentication auth) {
        Long userId = getUserId(auth);
        if (userId == null) return "redirect:/login";

        Optional<MoodEntry> entryOptional = moodEntryService.findById(id);

        if (entryOptional.isPresent()) {
            if (!entryOptional.get().getUserId().equals(userId)) {
                return "redirect:/access-denied";
            }
        }

        return prepareRemedyModel(entryOptional, model);
    }

    /**
     * Private helper to populate the model with remedy data.
     */
    private String prepareRemedyModel(Optional<MoodEntry> entryOptional, Model model) {
        if (entryOptional.isEmpty()) {
            model.addAttribute("error", "No journal entries found to generate a remedy.");
            return "remedy";
        }

        MoodEntry entry = entryOptional.get();
        int mood = entry.getMoodLevel();
        int stress = entry.getStressLevel();

        // --- UPDATED LOGIC ---
        // Get the new Lists of Suggestions
        List<AnalyticsService.Suggestion> moodRemedies = analyticsService.getMoodRemedies(mood);
        List<AnalyticsService.Suggestion> stressRemedies = analyticsService.getStressRemedies(stress);
        String overallSuggestion = analyticsService.suggestionFor(new AnalyticsService.Averages(mood, stress));

        model.addAttribute("moodScore", mood);
        model.addAttribute("stressScore", stress);
        model.addAttribute("moodRemedies", moodRemedies); // Pass the list
        model.addAttribute("stressRemedies", stressRemedies); // Pass the list
        model.addAttribute("overallSuggestion", overallSuggestion);
        model.addAttribute("entryDate", entry.getDate());

        return "remedy";
    }
}
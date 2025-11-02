package com.famcare.controller;

import com.famcare.model.MoodEntry;
import com.famcare.model.User; // IMPORT YOUR USER MODEL
import com.famcare.repository.UserRepository; // IMPORT YOUR USER REPOSITORY
import com.famcare.service.AnalyticsService;
import com.famcare.service.MoodEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // ADD THIS IMPORT

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {
    private final MoodEntryService moodEntryService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(MoodEntryService moodEntryService,
                               AnalyticsService analyticsService,
                               UserRepository userRepository) {
        this.moodEntryService = moodEntryService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    /**
     * Helper method to get the real, stable User ID from the database.
     */
    private Long getUserId(Authentication auth) {
        if (auth == null) {
            return null;
        }

        // FIX: Handle the Optional<User> returned by the repository
        Optional<User> userOptional = userRepository.findByUsername(auth.getName());

        if (userOptional.isPresent()) {
            return userOptional.get().getId(); // Return the real ID
        }

        return null; // User not found
    }

    @GetMapping("/me")
    public String myAnalytics(Model model, Authentication auth) {
        Long userId = getUserId(auth);
        if (userId == null) {
            return "redirect:/login"; // User not found, send to login
        }

        List<MoodEntry> recent = moodEntryService.findRecentByUser(userId);
        var avg = analyticsService.averagesForEntries(recent);

        model.addAttribute("avgMood", avg.averageMood());
        model.addAttribute("avgStress", avg.averageStress());
        model.addAttribute("suggestion", analyticsService.suggestionFor(avg));

        return "analytics";
    }

    @GetMapping("/family/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> familyAnalytics(@PathVariable Long id) {
        // ... (existing family logic)
        List<MoodEntry> allRecent = moodEntryService.findRecentByUser(id);
        var grouped = analyticsService.groupByUser(allRecent);
        var famAvg = analyticsService.familyAverages(grouped);
        Map<String, Object> res = new HashMap<>();
        res.put("familyName", "Family " + id);
        res.put("averageMood", famAvg.averageMood());
        res.put("averageStress", famAvg.averageStress());
        res.put("suggestion", analyticsService.suggestionFor(famAvg));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> myAnalyticsData(Authentication auth) {
        Long userId = getUserId(auth);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        List<MoodEntry> recentEntries = moodEntryService.findRecentByUser(userId);

        Map<String, Object> chartData = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> moods = new ArrayList<>();
        List<Integer> stresses = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        for (int i = recentEntries.size() - 1; i >= 0; i--) {
            MoodEntry entry = recentEntries.get(i);
            dates.add(entry.getDate().format(formatter));
            moods.add(entry.getMoodLevel());
            stresses.add(entry.getStressLevel());
        }

        chartData.put("labels", dates);
        chartData.put("moods", moods);
        chartData.put("stresses", stresses);

        return ResponseEntity.ok(chartData);
    }
}
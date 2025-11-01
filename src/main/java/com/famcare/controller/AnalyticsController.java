package com.famcare.controller;

import com.famcare.model.MoodEntry;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {
    private final MoodEntryService moodEntryService;
    private final AnalyticsService analyticsService;

    public AnalyticsController(MoodEntryService moodEntryService, AnalyticsService analyticsService) {
        this.moodEntryService = moodEntryService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/me")
    public String myAnalytics(Model model, Authentication auth) {
        Long userId = Math.abs(auth.getName().hashCode()) + 0L;
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
        // Demo: combine all recent entries regardless of membership mapping for simplicity
        // In a real app, fetch members by family, then fetch entries by each member userId
        List<MoodEntry> allRecent = moodEntryService.findRecentByUser(id); // placeholder
        var grouped = analyticsService.groupByUser(allRecent);
        var famAvg = analyticsService.familyAverages(grouped);
        Map<String, Object> res = new HashMap<>();
        res.put("familyName", "Family " + id);
        res.put("averageMood", famAvg.averageMood());
        res.put("averageStress", famAvg.averageStress());
        res.put("suggestion", analyticsService.suggestionFor(famAvg));
        return ResponseEntity.ok(res);
    }
}

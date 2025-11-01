package com.famcare.service;

import com.famcare.model.MoodEntry;
import com.famcare.model.FamilyMember;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    public record Averages(double averageMood, double averageStress) {}

    public Averages averagesForEntries(List<MoodEntry> entries) {
        if (entries == null || entries.isEmpty()) return new Averages(0, 0);
        DoubleSummaryStatistics mood = entries.stream().collect(Collectors.summarizingDouble(MoodEntry::getMoodLevel));
        DoubleSummaryStatistics stress = entries.stream().collect(Collectors.summarizingDouble(MoodEntry::getStressLevel));
        return new Averages(round(mood.getAverage()), round(stress.getAverage()));
    }

    public Averages familyAverages(Map<Long, List<MoodEntry>> entriesByUser) {
        List<MoodEntry> all = entriesByUser.values().stream().flatMap(List::stream).toList();
        return averagesForEntries(all);
    }

    public String suggestionFor(Averages avg) {
        if (avg.averageMood() >= 7 && avg.averageStress() <= 4) return "Mood improving — keep it up!";
        if (avg.averageStress() >= 6 && avg.averageMood() <= 5) return "Stress levels rising — consider relaxation or a family walk.";
        return "Stable week — maintain current routines.";
    }

    public Map<Long, List<MoodEntry>> groupByUser(List<MoodEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(MoodEntry::getUserId));
    }

    private double round(double v) { return Math.round(v * 10.0) / 10.0; }
}

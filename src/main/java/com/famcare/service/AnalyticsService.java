package com.famcare.service;

import com.famcare.model.MoodEntry;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    public record Averages(double averageMood, double averageStress) {}
    public record Suggestion(String title, String description) {}


    public Averages averagesForEntries(List<MoodEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return new Averages(0.0, 0.0);
        }
        double moodSum = entries.stream().mapToDouble(MoodEntry::getMoodLevel).sum();
        double stressSum = entries.stream().mapToDouble(MoodEntry::getStressLevel).sum();
        return new Averages(moodSum / entries.size(), stressSum / entries.size());
    }

    public Map<Long, List<MoodEntry>> groupByUser(List<MoodEntry> entries) {
        return entries.stream().collect(Collectors.groupingBy(MoodEntry::getUserId));
    }

    public Averages familyAverages(Map<Long, List<MoodEntry>> grouped) {
        List<MoodEntry> allEntries = grouped.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return averagesForEntries(allEntries);
    }

    public String suggestionFor(Averages avg) {
        if (avg.averageMood() > 7 && avg.averageStress() < 4) {
            return "Positive outlook, low stress. Keep up the great work!";
        } else if (avg.averageMood() < 4 && avg.averageStress() > 7) {
            return "High stress and low mood detected. Prioritize self-care and consider talking to someone.";
        } else if (avg.averageMood() < 5) {
            return "Your mood seems low. Try to connect with someone or do an activity you usually enjoy.";
        } else if (avg.averageStress() > 6) {
            return "Stress levels seem high. Remember to take short breaks and practice deep breathing.";
        } else {
            return "Stable week — maintain current routines.";
        }
    }

    // --- UPDATED METHOD: More granular brackets ---
    public List<Suggestion> getMoodRemedies(int mood) {
        if (mood == 10) {
            return List.of(
                    new Suggestion("Savor the Moment", "You're feeling amazing. Take a 'mental snapshot' of this feeling. This helps build resilience."),
                    new Suggestion("Share the Positivity", "Use your great energy to brighten someone else's day. Give a genuine compliment.")
            );
        } else if (mood >= 8) {
            return List.of(
                    new Suggestion("Channel this Energy", "You're feeling great! This is a perfect time to tackle a creative project or do a physical activity you love."),
                    new Suggestion("Connect with Others", "Your positive energy is contagious. Reach out to a friend or family member and share your good mood.")
            );
        } else if (mood >= 6) {
            return List.of(
                    new Suggestion("Practice Gratitude", "Write down three specific things you are grateful for today, no matter how small (e.g., 'the good cup of coffee I had')."),
                    new Suggestion("Maintain Routine", "You're in a stable, positive space. Keep up the good habits that got you here, like your sleep schedule.")
            );
        } else if (mood >= 4) {
            return List.of(
                    new Suggestion("Plan Something Fun", "You're feeling okay, but could use a boost. Plan a small, enjoyable event for this week, like watching a specific movie or cooking a favorite meal."),
                    new Suggestion("Gentle Movement", "Try some light exercise, like a 15-minute walk, or listen to an uplifting playlist.")
            );
        } else if (mood >= 2) {
            return List.of(
                    new Suggestion("Try 'Behavioral Activation'", "Choose one small activity you *used* to enjoy (like listening to an old album, drawing, or stretching) and do it for just 10 minutes."),
                    new Suggestion("Get 15 Minutes of Sunlight", "If possible, step outside. Sunlight is a natural mood booster. If not, sit by a bright window.")
            );
        } else {
            return List.of(
                    new Suggestion("Focus on One Small Win", "This is a tough day. Focus on one simple, achievable task, like making your bed or taking a shower. Acknowledge it as a win."),
                    new Suggestion("Try the '5-4-3-2-1' Method", "Ground yourself by naming 5 things you can see, 4 things you can feel, 3 things you can hear, 2 things you can smell, and 1 thing you can taste."),
                    new Suggestion("Reach Out (Gently)", "Send a simple text to a friend or family member. It doesn't have to be a deep conversation, just a simple 'hello' to feel connected.")
            );
        }
    }

    // --- UPDATED METHOD: More granular brackets ---
    public List<Suggestion> getStressRemedies(int stress) {
        if (stress >= 9) {
            return List.of(
                    new Suggestion("Practice 4-7-8 Breathing", "This is for immediate relief. Inhale for 4 seconds, hold your breath for 7 seconds, and exhale slowly for 8 seconds. Repeat 5 times."),
                    new Suggestion("Reduce Sensory Input", "Step away from screens. Go to a quiet, dim room and sit for a few minutes to reduce stimulation and reset.")
            );
        } else if (stress >= 7) {
            return List.of(
                    new Suggestion("Engage in a 'Brain Dump'", "Take 5 minutes and write down *everything* on your mind. Getting it out of your head and onto paper can make it feel manageable."),
                    new Suggestion("Do a 5-Minute Tense-and-Release", "Clench your fists tightly for 5 seconds, then release. Do the same with your shoulders, tensing them up to your ears, then releasing.")
            );
        } else if (stress == 6) {
            return List.of(
                    new Suggestion("Organize Your Immediate Space", "A cluttered space can contribute to a cluttered mind. Spend 10 minutes tidying up just your desk or room."),
                    new Suggestion("Prioritize Your Tasks", "You have a lot to do. Write down all your tasks and identify the 'Top 1' that MUST be done. Focus only on that.")
            );
        } else if (stress >= 4) {
            return List.of(
                    new Suggestion("Take a 10-Minute Walk", "A short walk can clear your head and prevent stress from building up. Try to do this without your phone if possible."),
                    new Suggestion("Plan Your Next Day", "Write down your top 3 priorities for tomorrow. This can prevent morning anxiety about what needs to be done.")
            );
        } else if (stress >= 2) {
            return List.of(
                    // --- THIS IS THE LINE I FIXED ---
                    // I escaped the quotes around "worry," "I'll," and "5 PM."
                    new Suggestion("Schedule a 'Worry Time'", "Set aside 10 minutes later in the day to \"worry.\" If a stressful thought comes up, tell yourself, \"I'll think about this at 5 PM.\" This helps control intrusive thoughts."),
                    new Suggestion("Check Your Posture", "Are you hunched over? Sit up straight, roll your shoulders back, and take a deep breath. Poor posture can increase feelings of stress.")
            );
        } else {
            return List.of(
                    new Suggestion("Acknowledge Your Calm", "Your stress is well-managed. Take a moment to recognize what's working—is it good sleep, organization, or boundaries? Keep it up.")
            );
        }
    }
}
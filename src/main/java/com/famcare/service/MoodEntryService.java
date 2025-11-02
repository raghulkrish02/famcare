package com.famcare.service;

import com.famcare.model.MoodEntry;
import com.famcare.repository.MoodEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // ADD THIS IMPORT

@Service
public class MoodEntryService {
    private final MoodEntryRepository moodEntryRepository;

    public MoodEntryService(MoodEntryRepository moodEntryRepository) {
        this.moodEntryRepository = moodEntryRepository;
    }

    public MoodEntry save(MoodEntry entry) { return moodEntryRepository.save(entry); }

    // RENAMED from delete() to deleteById() to match MoodController
    public void deleteById(Long id) {
        moodEntryRepository.deleteById(id);
    }

    // ADDED this method for MoodController
    public Optional<MoodEntry> findById(Long id) {
        return moodEntryRepository.findById(id);
    }

    public List<MoodEntry> findByUser(Long userId) { return moodEntryRepository.findByUserIdOrderByDateDesc(userId); }
    public List<MoodEntry> findRecentByUser(Long userId) { return moodEntryRepository.findTop7ByUserIdOrderByDateDesc(userId); }
    public List<MoodEntry> findByUserAndRange(Long userId, LocalDate start, LocalDate end) { return moodEntryRepository.findByUserIdAndDateBetween(userId, start, end); }
    // Add this method to your existing MoodEntryService.java
    public List<MoodEntry> findByUserIdIn(List<Long> userIds) {
        return moodEntryRepository.findByUserIdInOrderByDateDesc(userIds);
    }
}

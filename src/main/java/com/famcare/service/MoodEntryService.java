package com.famcare.service;

import com.famcare.model.MoodEntry;
import com.famcare.repository.MoodEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MoodEntryService {
    private final MoodEntryRepository moodEntryRepository;

    public MoodEntryService(MoodEntryRepository moodEntryRepository) {
        this.moodEntryRepository = moodEntryRepository;
    }

    public MoodEntry save(MoodEntry entry) { return moodEntryRepository.save(entry); }
    public void delete(Long id) { moodEntryRepository.deleteById(id); }
    public List<MoodEntry> findByUser(Long userId) { return moodEntryRepository.findByUserIdOrderByDateDesc(userId); }
    public List<MoodEntry> findRecentByUser(Long userId) { return moodEntryRepository.findTop7ByUserIdOrderByDateDesc(userId); }
    public List<MoodEntry> findByUserAndRange(Long userId, LocalDate start, LocalDate end) { return moodEntryRepository.findByUserIdAndDateBetween(userId, start, end); }
}

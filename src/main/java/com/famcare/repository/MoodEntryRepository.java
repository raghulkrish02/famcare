package com.famcare.repository;

import com.famcare.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserIdOrderByDateDesc(Long userId);
    List<MoodEntry> findTop7ByUserIdOrderByDateDesc(Long userId);
    List<MoodEntry> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}

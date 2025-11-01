package com.famcare.controller;

import com.famcare.model.MoodEntry;
import com.famcare.service.MoodEntryService;
import com.famcare.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/journal")
public class MoodController {
    private final MoodEntryService moodEntryService;
    private final UserService userService;

    public MoodController(MoodEntryService moodEntryService, UserService userService) {
        this.moodEntryService = moodEntryService;
        this.userService = userService;
    }

    @GetMapping
    public String journal(Model model, Authentication auth) {
        Long userId = userService.userIdFor(auth.getName());
        model.addAttribute("entry", new MoodEntry());
        model.addAttribute("entries", moodEntryService.findByUser(userId));
        return "journal";
    }

    @PostMapping
    public String addEntry(@ModelAttribute MoodEntry entry, Authentication auth) {
        Long userId = userService.userIdFor(auth.getName());
        entry.setUserId(userId);
        if (entry.getDate() == null) entry.setDate(LocalDate.now());
        moodEntryService.save(entry);
        return "redirect:/journal";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        moodEntryService.delete(id);
        return "redirect:/journal";
    }
}

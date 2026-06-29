package com.zerohour.backend.controller;

import com.zerohour.backend.ai.GoogleCalendarService;
import com.zerohour.backend.ai.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
public class CalendarController {

    @Autowired GoogleCalendarService calendarService;
    @Autowired GeminiService geminiService;

    // Get Google login URL
    @GetMapping("/auth-url")
    public Map<String, String> getAuthUrl() {
        return Map.of("url", calendarService.getAuthUrl());
    }

    // Handle OAuth callback
    @GetMapping("/callback")
    public String handleCallback(@RequestParam String code) {
        try {
            String token = calendarService.exchangeCodeForToken(code);
            calendarService.saveToken(1L, token); // userId = 1 for now
            return "<h2>✅ Google Calendar Connected!</h2><p>You can close this tab.</p>";
        } catch (Exception e) {
            return "<h2>❌ Error: " + e.getMessage() + "</h2>";
        }
    }

    // Add task to calendar
    @PostMapping("/add-event")
    public Map<String, String> addEvent(@RequestBody Map<String, String> body) {
        try {
            String token = calendarService.getToken(1L);
            if (token == null) return Map.of("error", "Not connected to Google Calendar");

            LocalDateTime start = LocalDateTime.parse(body.get("start"));
            LocalDateTime end = start.plusHours(
                Integer.parseInt(body.getOrDefault("hours", "1"))
            );

            String link = calendarService.addTaskToCalendar(
                token, body.get("title"), start, end
            );
            return Map.of("success", "true", "link", link);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    // Get AI schedule using calendar busy slots
   @GetMapping("/smart-schedule/{userId}")
public Map<String, String> getSmartSchedule(@PathVariable Long userId) {
    try {
        String token = calendarService.getToken(userId);
        if (token == null) return Map.of("error", "Not connected");
        List<String> busySlots = calendarService.getBusySlots(token);
        return Map.of("schedule", "Busy slots: " + busySlots.toString());
    } catch (Exception e) {
        return Map.of("error", e.getMessage());
    }
}

@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
class AuthController {

    @org.springframework.beans.factory.annotation.Autowired
    GoogleCalendarService calendarService;

    @org.springframework.web.bind.annotation.GetMapping("/auth/google/callback")
    public String handleGoogleCallback(
            @org.springframework.web.bind.annotation.RequestParam String code) {
        try {
            String token = calendarService.exchangeCodeForToken(code);
            calendarService.saveToken(1L, token);
            return "<html><body style='font-family:sans-serif;text-align:center;padding:50px;background:#1a0533;color:white'>" +
                   "<h1 style='color:#a78bfa'>✅ Google Calendar Connected!</h1>" +
                   "<p>You can close this tab.</p></body></html>";
        } catch (Exception e) {
            return "<html><body><h2>❌ Error: " + e.getMessage() + "</h2></body></html>";
        }
    }
}
}
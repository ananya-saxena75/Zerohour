package com.zerohour.backend.controller;

import com.zerohour.backend.model.Task;
import com.zerohour.backend.repository.TaskRepository;
import com.zerohour.backend.ai.GeminiService;
import com.zerohour.backend.ai.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private GeminiService geminiService;
    @Autowired private GoogleCalendarService calendarService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        Task saved = taskRepository.save(task);
        try {
            String token = calendarService.getToken(task.getUserId());
            System.out.println("Calendar token: " + (token != null ? "found" : "NULL"));
            if (token != null && saved.getDeadline() != null) {
                System.out.println("Adding to calendar: " + saved.getTitle());
                String link = calendarService.addTaskToCalendar(
                    token, saved.getTitle(),
                    saved.getDeadline().minusHours(2),
                    saved.getDeadline()
                );
                System.out.println("✅ Calendar event: " + link);
            } else {
                System.out.println("Skipping - token: " + (token!=null) + ", deadline: " + saved.getDeadline());
            }
        } catch (Exception e) {
            System.out.println("❌ Calendar sync failed: " + e.getMessage());
        }
        return saved;
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping("/prioritize")
    public ResponseEntity<String> prioritize(@RequestParam String tasks) {
        return ResponseEntity.ok(geminiService.prioritizeTasks(tasks));
    }

    @GetMapping("/breakdown")
    public ResponseEntity<String> breakdown(
            @RequestParam String title,
            @RequestParam String deadline,
            @RequestParam float hours) {
        return ResponseEntity.ok(geminiService.breakdownTask(title, deadline, hours));
    }
}
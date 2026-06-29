package com.zerohour.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime deadline;
    private Float estimatedHours;
    private Integer importance = 5;
    private Float priorityScore;
    private Integer postponedCount = 0;
    private Integer completionPercent = 0;
    private String status = "PENDING";

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Float getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Float estimatedHours) { this.estimatedHours = estimatedHours; }
    public Integer getImportance() { return importance; }
    public void setImportance(Integer importance) { this.importance = importance; }
    public Float getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Float priorityScore) { this.priorityScore = priorityScore; }
    public Integer getPostponedCount() { return postponedCount; }
    public void setPostponedCount(Integer postponedCount) { this.postponedCount = postponedCount; }
    public Integer getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(Integer completionPercent) { this.completionPercent = completionPercent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
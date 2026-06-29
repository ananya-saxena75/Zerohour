package com.zerohour.backend.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String callGemini(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + "?key=" + apiKey;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List<Map> candidates = (List<Map>) response.getBody().get("candidates");
        Map contentMap = (Map) candidates.get(0).get("content");
        List<Map> parts = (List<Map>) contentMap.get("parts");
        return (String) parts.get(0).get("text");
    }

    public String prioritizeTasks(String taskList) {
        String prompt = """
            You are a productivity AI. Prioritize these tasks by urgency and importance.
            Tasks: %s
            Return a JSON array: [{"title":"...","priority_score":9.2,"reason":"..."}]
            Only return valid JSON, nothing else.
            """.formatted(taskList);
        return callGemini(prompt);
    }

    public String breakdownTask(String taskTitle, String deadline, float hours) {
        String prompt = """
            Break this task into subtasks:
            Task: %s, Deadline: %s, Estimated hours: %.1f
            Return JSON: [{"subtask":"...","estimated_minutes":30,"order":1}]
            Only return valid JSON, nothing else.
            """.formatted(taskTitle, deadline, hours);
        return callGemini(prompt);
    }
}

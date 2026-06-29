package com.zerohour.backend.ai;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GoogleCalendarService {

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    @Autowired
    private DataSource dataSource;

    private static final List<String> SCOPES =
        Collections.singletonList("https://www.googleapis.com/auth/calendar");

    public String getAuthUrl() {
        GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(
            clientId, redirectUri, SCOPES
        );
        return url.setAccessType("offline").build();
    }

  public String exchangeCodeForToken(String code) throws Exception {
    GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
        GoogleNetHttpTransport.newTrustedTransport(),
        GsonFactory.getDefaultInstance(),
        clientId, clientSecret, code, redirectUri
    ).execute();
    
    String token = response.getRefreshToken();
    if (token == null) {
        token = response.getAccessToken();
        System.out.println("No refresh token, using access token");
    }
    System.out.println("Token type: " + (response.getRefreshToken() != null ? "REFRESH" : "ACCESS"));
    return token;
}

    private Calendar getCalendarService(String refreshToken) throws Exception {
        GoogleCredential credential = new GoogleCredential.Builder()
            .setTransport(GoogleNetHttpTransport.newTrustedTransport())
            .setJsonFactory(GsonFactory.getDefaultInstance())
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(refreshToken);

        return new Calendar.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("ZeroHour").build();
    }

    public String addTaskToCalendar(String refreshToken, String taskTitle,
                                     LocalDateTime start, LocalDateTime end) throws Exception {
        Calendar service = getCalendarService(refreshToken);

        Event event = new Event()
            .setSummary("⚡ " + taskTitle)
            .setDescription("ZeroHour Task - Stay focused!");

        DateTime startTime = new DateTime(
            Date.from(start.atZone(ZoneId.systemDefault()).toInstant())
        );
        DateTime endTime = new DateTime(
            Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
        );

        event.setStart(new EventDateTime().setDateTime(startTime));
        event.setEnd(new EventDateTime().setDateTime(endTime));

        EventReminder reminder = new EventReminder()
            .setMethod("popup").setMinutes(30);
        Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(Collections.singletonList(reminder));
        event.setReminders(reminders);

        Event created = service.events().insert("primary", event).execute();
        return created.getHtmlLink();
    }

    public List<String> getBusySlots(String refreshToken) throws Exception {
        Calendar service = getCalendarService(refreshToken);
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime tomorrow = new DateTime(System.currentTimeMillis() + 86400000L);

        Events events = service.events().list("primary")
            .setTimeMin(now).setTimeMax(tomorrow)
            .setOrderBy("startTime").setSingleEvents(true).execute();

        List<String> busySlots = new ArrayList<>();
        for (Event event : events.getItems()) {
            busySlots.add(event.getSummary() + ": " +
                event.getStart().getDateTime() + " - " +
                event.getEnd().getDateTime());
        }
        return busySlots;
    }

    // Save token to database
    public void saveToken(Long userId, String token) {
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(
                 "UPDATE users SET calendar_token=? WHERE id=?")) {
            stmt.setString(1, token);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
            System.out.println("✅ Token saved to DB for user " + userId);
        } catch (Exception e) {
            System.out.println("❌ Token save failed: " + e.getMessage());
        }
    }

    // Get token from database
    public String getToken(Long userId) {
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(
                 "SELECT calendar_token FROM users WHERE id=?")) {
            stmt.setLong(1, userId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                String token = rs.getString("calendar_token");
                System.out.println("✅ Token retrieved for user " + userId +
                    ": " + (token != null ? "exists" : "null"));
                return token;
            }
        } catch (Exception e) {
            System.out.println("❌ Token get failed: " + e.getMessage());
        }
        return null;
    }
}
package com.example.Calmora.google;

import org.springframework.stereotype.Service;

@Service
public class MeetService {
    public String createMeetLink(String email) {
        return "https://meet.google.com/lookup/" + email.hashCode();
    }
}
